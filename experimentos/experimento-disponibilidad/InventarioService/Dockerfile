FROM python:3.9-slim

WORKDIR /app

# Copiar archivo de requerimientos e instalar dependencias
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el resto del código fuente (incluye app.py, modelos, migraciones, etc.)
COPY . .

# Exponer el puerto en el que se ejecuta la aplicación (en desarrollo Flask usa 5000, pero en Cloud Run se recomienda 8080)
EXPOSE 5000
