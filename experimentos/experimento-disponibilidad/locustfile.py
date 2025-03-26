from locust import HttpUser, task, between

GCP_IP = "35.239.230.106"
LOCALHOST_IP = "localhost"

class MonitorUser(HttpUser):
    host = "http://" + GCP_IP + ":3002"  # Monitor
    wait_time = between(1, 2)

    @task
    def ping_monitor(self):
        self.client.get("/monitor")

class InventarioUser(HttpUser):
    host = "http://" + GCP_IP + ":3001"  # InventarioService
    wait_time = between(1, 2)

    @task
    def obtener_inventario(self):
        self.client.get("/inventario")