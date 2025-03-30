import pytest
import sys
import os
import importlib
import json
from unittest.mock import patch, Mock
from functools import wraps

from app import create_app
from app.lib.database import db
from flask import request, g

# Define test authentication functions
def mock_auth_decorator_generator(original_decorator):
    """Generate a mock auth decorator that preserves the original's signature"""
    @wraps(original_decorator)
    def mock_decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            # Set test user in request
            request.user = {
                'id': 1,
                'username': 'test_user',
                'rol': 'ADMINISTRADOR'
            }
            return f(*args, **kwargs)
        return decorated_function
    return mock_decorator

@pytest.fixture(scope='session')
def app():
    """Create and configure a Flask app for testing."""
    # Import the auth module - we need the actual functions to mock
    import app.lib.auth

    # Save original functions
    original_validate_token = app.lib.auth.validate_token

    # Create mock that matches the original signature
    # This mock will always bypass authentication and set the user
    def mock_validate_token(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            # Always set a test user for every authenticated request
            request.user = {
                'id': 1,
                'username': 'test_user',
                'rol': 'ADMINISTRADOR'
            }
            return f(*args, **kwargs)
        return decorated_function

    # Patch the auth module functions
    with patch('app.lib.auth.validate_token', mock_validate_token):
        # Create the app
        testing_app = create_app()
        testing_app.config['TESTING'] = True
        testing_app.config['USERS_SERVICE_URL'] = 'http://test-users-service'

        yield testing_app


@pytest.fixture
def client(app):
    """Basic test client fixture."""
    return app.test_client()


@pytest.fixture
def auth_client(app):
    """
    Test client with authentication headers pre-configured.
    """
    # Create a test client
    return app.test_client()


class MockResponse:
    def __init__(self, json_data, status_code):
        self.json_data = json_data
        self.status_code = status_code

    def json(self):
        return self.json_data

@pytest.fixture(autouse=True)
def mock_requests(monkeypatch):
    """Mock the requests.get method to return a successful auth response"""
    def mock_get(*args, **kwargs):
        # Always return a successful auth response for any URL
        # This ensures authentication is always successful in tests
        return MockResponse({
            'id': 1,
            'username': 'test_user',
            'rol': 'ADMINISTRADOR'
        }, 200)

    # Apply the monkeypatch to replace requests.get with our mock
    import requests
    monkeypatch.setattr(requests, "get", mock_get)

@pytest.fixture
def db_session(app):
    with app.app_context():
        db.create_all()
        yield db.session
        db.session.remove()
        db.drop_all()
