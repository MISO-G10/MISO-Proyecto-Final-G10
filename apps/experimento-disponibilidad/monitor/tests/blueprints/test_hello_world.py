class TestHelloWorld:
    def test_hello_world(self):
        assert True

    def test_get_hello_world(self, client):
        response = client.get('/disponibilidad-monitor/hello')

        assert response.status_code == 200

        assert response.json == {'message': 'monitor says hello!'}
