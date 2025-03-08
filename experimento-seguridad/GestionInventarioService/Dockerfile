# Usa una imagen ligera de Python
FROM python:3.9-slim

# Define el directorio de trabajo
WORKDIR /app

# Copiar archivo de requerimientos e instalar dependencias
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el resto del código fuente
COPY . .

# Configurar variables de entorno para Flask
ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_RUN_PORT=5001

# Exponer el puerto en el que correrá la app
EXPOSE 5001

# Comando para iniciar la aplicación
CMD ["flask", "run"]
