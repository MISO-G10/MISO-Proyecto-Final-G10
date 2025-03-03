FROM python:3.9-slim

WORKDIR /app

# Instalar dependencias
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el código fuente (incluye app.py, donde se define la lógica del monitor)
COPY . .

EXPOSE 5000
