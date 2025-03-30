from locust import HttpUser, task, between

GCP_IP = "127.0.0.1" # Temporal mientras se hace el deploy a producción en GCP
LOCALHOST_IP = "localhost"

class Usuario(HttpUser):
    host = "http://" + LOCALHOST_IP + ":3000"  # Usuarios
    wait_time = between(1, 2)

    @task
    def ping_usuario(self):
        self.client.get("/usuarios/ping")

class Fabricante(HttpUser):
    host = "http://" + LOCALHOST_IP + ":3001"  # Fabricantes
    wait_time = between(1, 2)

    @task
    def ping_fabricante(self):
        self.client.get("/fabricantes/ping")