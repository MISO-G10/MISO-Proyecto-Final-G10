from app.models.user import User


class TestHelloWorld:
    def test_hello_world(self):
        assert True

    def test_get_hello_world(self, client):
        response = client.get('/exp-01/hello')

        assert response.status_code == 200

        assert response.json == {'message': 'Hello, world!'}

    def test_get_hello_protected(self, client):
        response = client.get('/exp-01/hello-protected')

        assert response.status_code == 401

    def test_post_hello_create_jwt(self, client, db_session):
        user = User(
            name='test',
            email='jhon@doe.com'
        )

        db_session.add(user)
        db_session.commit()

        response = client.post('/exp-01/hello-create-jwt')

        assert response.status_code == 200

        assert 'access_token' in response.json

    def test_get_hello_protected_with_jwt(self, client, db_session):
        user = User(
            name='test',
            email='jhon@doe.com'
        )

        db_session.add(user)
        db_session.commit()

        response = client.post('/exp-01/hello-create-jwt')

        access_token = response.json['access_token']

        response = client.get('/exp-01/hello-protected', headers={
            'Authorization': f'Bearer {access_token}'
        })

        assert response.status_code == 200
