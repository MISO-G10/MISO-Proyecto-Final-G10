from locust import HttpUser, task, between

class MonitorUser(HttpUser):
    host = "http://localhost:3002"  # Monitor
    wait_time = between(1, 2)

    @task
    def ping_monitor(self):
        self.client.get("/monitor")

class InventarioUser(HttpUser):
    host = "http://localhost:3001"  # InventarioService
    wait_time = between(1, 2)

    @task
    def obtener_inventario(self):
        self.client.get("/inventario")