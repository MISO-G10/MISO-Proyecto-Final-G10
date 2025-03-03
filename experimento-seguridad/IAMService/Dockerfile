FROM python:3.11-slim-buster
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 5000
# Ejecutar la aplicación Flask (para exponer el endpoint /monitor)
CMD ["flask", "run", "--host=0.0.0.0"]