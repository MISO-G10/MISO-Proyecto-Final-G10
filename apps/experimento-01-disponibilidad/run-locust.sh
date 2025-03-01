#!/bin/bash

# Verificar si existe el entorno virtual "venv". Si no existe, crearlo.
if [ ! -d "venv" ]; then
    echo "No se encontró el entorno virtual. Creando uno..."
    python3 -m venv venv
fi

# Activar el entorno virtual
echo "Activando el entorno virtual..."
source venv/bin/activate

# Instalar Locust (si aún no está instalado)
pip install --upgrade pip
pip install locust

# Ejecutar Locust con el archivo de pruebas (locustfile.py) y especificar el host
echo "Ejecutando Locust..."
locust -f locustfile.py --host http://localhost:3003