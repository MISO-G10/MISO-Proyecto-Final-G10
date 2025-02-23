class TestHelloWorld:
    def test_hello_world(self):
        assert True

    def test_get_hello_world(self, client):
        response = client.get('/{{ cookiecutter.project_slug }}/hello')

        assert response.status_code == 200

        assert response.json == {'message': '{{ cookiecutter.project_name }} says hello!'}
