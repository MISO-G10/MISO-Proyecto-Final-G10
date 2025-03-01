class TestHelloWorld:
    def test_hello_world(self):
        assert True

    def test_get_hello_world(self, client):
        response = client.get('/gestion-inventario-service/hello')

        assert response.status_code == 200

        assert response.json == {'message': 'gestion-inventario-service says hello!'}
