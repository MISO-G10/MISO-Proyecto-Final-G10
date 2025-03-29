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
    original_director_required = app.lib.auth.director_required
    
    # Create mocks that match the original signatures
    mock_validate_token = mock_auth_decorator_generator(original_validate_token)
    mock_director_required = mock_auth_decorator_generator(original_director_required)
    
    # Patch the auth module functions
    auth_patches = [
        patch('app.lib.auth.validate_token', mock_validate_token),
        patch('app.lib.auth.director_required', mock_director_required)
    ]
    
    # Apply all patches
    for p in auth_patches:
        p.start()
    
    # Create the app
    testing_app = create_app()
    testing_app.config['TESTING'] = True
    testing_app.config['USERS_SERVICE_URL'] = 'http://test-users-service'
    
    # Store the auth module for reference
    testing_app.auth_module = app.lib.auth
    
    yield testing_app
    
    # Stop all patches
    for p in auth_patches:
        p.stop()


@pytest.fixture
def client(app):
    """Basic test client fixture."""
    return app.test_client()


@pytest.fixture
def auth_client(app):
    """
    Test client with authentication headers pre-configured.
    This is a more robust approach for HTTP endpoint tests.
    """
    # Create a test client with the auth headers configured
    with app.test_client() as client:
        # Set a custom request context handler to inject auth data
        def before_request():
            from flask import request
            request.user = {
                'id': 1,
                'username': 'test_user',
                'rol': 'ADMINISTRADOR'
            }
            
        # Register the before_request handler for this client only
        app.before_request_funcs.setdefault(None, []).append(before_request)
        
        yield client
        
        # Remove the before_request handler
        app.before_request_funcs[None].remove(before_request)


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
        if '/me' in args[0]:
            return MockResponse({
                'id': 1,
                'username': 'test_user',
                'rol': 'ADMINISTRADOR'
            }, 200)
        return MockResponse(None, 404)

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
