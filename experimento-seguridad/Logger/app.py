import os
import requests
import json
import csv
import jwt
from datetime import datetime,timedelta
from dotenv import load_dotenv
from time import sleep


load_dotenv()
GESTION_INVENTARIO_URL = os.environ.get('GESTION_INV_SERVICE_URL')
IAM_SERVICE_URL = os.environ.get('IAM_SERVICE_URL')
ENDPOINT_INV = os.environ.get('ENDPOINT_INV')
ENDPOINT_LOGIN = os.environ.get('ENDPOINT_LOGIN')
URL_LOGIN = f"{IAM_SERVICE_URL}{ENDPOINT_LOGIN}"  
URL_INVENTORY = f"{GESTION_INVENTARIO_URL}{ENDPOINT_INV}"  
# Archivo CSV que contiene los mismos datos de usuario que la bdd de IAM Service
CSV_FILE = "users.csv"
# Archivo de log para registrar las respuestas
LOG_FILE = "log_experimento.json"
# Almacenar los resultados del experimento
resultados = []
# Limpiar el archivo de log antes de iniciar el experimento
open(LOG_FILE, "w").close()

#Base para generar tokens invalidos
token_valido = ""

# Clave secreta que usa el IAM Service para validar los tokens
JWT_SECRET_KEY = "experimento-error"
ALGORITHM = "HS256"

def autenticar_usuario(username, password):
    """Realiza login en el IAM Service y devuelve el token de acceso."""
    payload = {"username": username, "password": password}
    response = requests.post(URL_LOGIN, json=payload)
    
    if response.status_code == 200:
        return response.json().get("token")
    return None

def consultar_inventario(token):
    """Consulta el servicio de inventario con el token obtenido."""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(URL_INVENTORY, headers=headers)
    return response.status_code, response.json() if response.status_code == 200 else response.text

def generar_tokens_invalidos(token_valido):
    """Genera tokens inválidos y expirados a partir de un token válido."""

    ## Decodificar el token válido sin validar la firma
    payload = jwt.decode(token_valido, options={"verify_signature": False})

    # Token expirado: Crear un nuevo token con fecha de expiración en el pasado
    payload["exp"] = datetime.utcnow() - timedelta(seconds=10)  # Expirado hace 10 segundos
    token_expirado = jwt.encode(payload, JWT_SECRET_KEY, algorithm=ALGORITHM)

    # Token inválido: Modificar manualmente el token válido (cambiando los últimos caracteres)
    token_invalido = token_valido[:-5] + "abcde"  # Se altera el final del token

    return {
        "token_expirado": f"Bearer {token_expirado}",
        "token_invalido": f"Bearer {token_invalido}",
        "sin_token": None
    }



# FASE 1: TOKENS VÁLIDOS Y DIFERENTES NIVELES DE SERVICIO
# Leer el archivo CSV con usuarios y contraseñas
with open(CSV_FILE, newline="", encoding="utf-8") as csvfile:
    reader = csv.DictReader(csvfile, delimiter=";")
    
    #for row in reader:
    for i, row in enumerate(reader):  # 🔹 `enumerate()` agrega un contador `i`
        if i >= 5:  # 🔹 Detiene el loop después de 5 iteraciones
            break
        username = row["username"]
        password = row["password"]
        expected_inv_status = int(row["estado_esperado"])  # Estado esperado del inventario

        log_entry = {
            "timestamp": datetime.now().isoformat(),
            "username": username,
            "expected_status": 200,
            "expected_inv_status": expected_inv_status,
            "actual_status": None,
            "actual_inv_status": None,
            "match_login": False,
            "match_inventory": False,
        }

        # Intentar autenticación
        token = autenticar_usuario(username, password)
        if not token:
            log_entry["actual_status"] = 401  # Fallo de autenticación
            log_entry["match_login"] = False
        else:
            log_entry["actual_status"] = 200  # Login exitoso
            log_entry["match_login"] = True
            token_valido = token

            # Consultar inventario con el token
            sleep(2)
            status_code, response_data = consultar_inventario(token)
            log_entry["actual_inv_status"] = status_code
            log_entry["match_inventory"] = expected_inv_status == status_code
            log_entry["inventory_response"] = response_data

        resultados.append(log_entry)
        print(f"🔍 {username}: Login Esperado {200}, Obtenido {log_entry['actual_status']} | Inventario Esperado {expected_inv_status}, Obtenido {log_entry['actual_inv_status']}")


# FASE 2: TOKENS INVÁLIDOS
# Diferentes tokens inválidos generados a partir de uno v{alido o vacíos para probar el sistema de autenticación
tokens_invalidos = generar_tokens_invalidos(token_valido)

for tipo, token in tokens_invalidos.items():
    headers = {"Authorization": f"Bearer {token}"} if token else {}
    response = requests.get(URL_INVENTORY, headers=headers)
    if tipo == 'sin_token':
        status_esperado = 401
    else: 
        status_esperado = 422
    log_entry = {
        "timestamp": datetime.now().isoformat(),
        "username": None,
        "expected_status": None,
        "expected_inv_status": status_esperado,
        "actual_status": None,
        "actual_inv_status": response.status_code,
        "match_login": False,
        "match_inventory": response.status_code == status_esperado,
        "inventory_response": response.json() if response.status_code == 200 else response.text,
    }

    resultados.append(log_entry)
    print(f"Token Esperado: {status_esperado}, Obtenido: {response.status_code}")




# Guardar el log en un archivo JSON
with open(LOG_FILE, "w", encoding="utf-8") as file:
    json.dump(resultados, file, indent=4, ensure_ascii=False)

print("Experimento de seguridad completado. Resultados guardados en 'log_experimento.json'")
