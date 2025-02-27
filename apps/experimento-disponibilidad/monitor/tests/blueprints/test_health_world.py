class TestHelloWorld:
    def test_hello_world(self):
        assert True

    def test_get_hello_world(self, client):
        response = client.get('/disponibilidad-monitor/health')

        assert response.status_code == 200

        assert response.json == {'status': 'ok'}
