from locust import HttpUser, task, between

class MonitorUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def ping(self):
        self.client.get("/monitor")