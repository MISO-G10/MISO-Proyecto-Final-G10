import json
from datetime import date, timedelta
import pytest

from app.models.sales_plan import SalesPlan
from app.lib.database import db


class TestSalesPlan:
    def test_create_sales_plan(self, auth_client, db_session):
        # With our mocked authentication in conftest.py, we don't need to get a real token
        # We'll add a dummy header that will be ignored
        auth_header = 'Bearer test_token'

        # Define test sales plan data
        today = date.today()
        end_date = today + timedelta(days=30)

        data = {
            "nombre": "Test Sales Plan",
            "descripcion": "A test sales plan",
            "valor_objetivo": 1000.0,
            "fecha_inicio": today.strftime('%Y-%m-%d'),
            "fecha_fin": end_date.strftime('%Y-%m-%d'),
            "seller_ids": [1, 2]  # Multiple seller IDs
        }

        # Create a sales plan
        response = auth_client.post(
            '/planes',
            json=data,
            headers={'Authorization': auth_header}
        )

        assert response.status_code == 201
        
        # Check that the response contains multiple plans (one for each seller)
        assert len(response.json['items']) == 2
        
        # Verify first plan details
        assert response.json['items'][0]['nombre'] == "Test Sales Plan"
        assert response.json['items'][0]['descripcion'] == "A test sales plan"
        assert float(response.json['items'][0]['valor_objetivo']) == 1000.0
        assert response.json['items'][0]['usuario_id'] == '1'  # First seller ID
        
        # Verify second plan details
        assert response.json['items'][1]['nombre'] == "Test Sales Plan"
        assert response.json['items'][1]['descripcion'] == "A test sales plan"
        assert float(response.json['items'][1]['valor_objetivo']) == 1000.0
        assert response.json['items'][1]['usuario_id'] == '2'  # Second seller ID

        # Check that the plans were created in the database
        sales_plans = db_session.execute(
            db.select(SalesPlan).where(SalesPlan.nombre == "Test Sales Plan")
        ).scalars().all()
        
        assert len(sales_plans) == 2
        
        # Verify each plan was created with the correct seller ID
        seller_ids = [plan.usuario_id for plan in sales_plans]
        assert '1' in seller_ids
        assert '2' in seller_ids

    def test_get_sales_plans(self, auth_client, db_session):
        # Create test sales plans
        today = date.today()
        end_date = today + timedelta(days=30)

        # Create two sales plans for different users
        sales_plan1 = SalesPlan(
            usuario_id='1',
            nombre="Test Sales Plan 1",
            descripcion="A test sales plan for user 1",
            valor_objetivo=1000.0,
            fecha_inicio=today.strftime('%Y-%m-%d'),
            fecha_fin=end_date.strftime('%Y-%m-%d')
        )
        
        sales_plan2 = SalesPlan(
            usuario_id='2',
            nombre="Test Sales Plan 2",
            descripcion="A test sales plan for user 2",
            valor_objetivo=2000.0,
            fecha_inicio=today.strftime('%Y-%m-%d'),
            fecha_fin=end_date.strftime('%Y-%m-%d')
        )

        db_session.add(sales_plan1)
        db_session.add(sales_plan2)
        db_session.commit()

        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'

        # Get all sales plans
        response = auth_client.get(
            '/planes',
            headers={'Authorization': auth_header}
        )

        assert response.status_code == 200
        assert len(response.json['items']) == 2
        
        # Check that both sales plans are in the response
        plan_names = [plan['nombre'] for plan in response.json['items']]
        assert "Test Sales Plan 1" in plan_names
        assert "Test Sales Plan 2" in plan_names
        
        # Verify that each plan has the usuario_id field
        for plan in response.json['items']:
            assert 'usuario_id' in plan
            assert plan['usuario_id'] in ['1', '2']

    def test_validation_invalid_date_http(self, auth_client, db_session):
        """
        Test validation of invalid dates through the HTTP endpoint.
        This tests that dates must be valid calendar dates.
        """
        # Define test data with invalid calendar date
        data = {
            "nombre": "Invalid Calendar Date Plan",
            "descripcion": "A sales plan with invalid calendar date",
            "valor_objetivo": 1000.0,
            "fecha_inicio": "2023-02-30",  # February 30th doesn't exist
            "fecha_fin": "2023-03-15",
            "seller_ids": [1]
        }

        # Add authorization header for testing
        auth_header = 'Bearer test_token'

        # Make a request to create a sales plan with invalid calendar date
        response = auth_client.post(
            '/planes',
            json=data,
            headers={'Authorization': auth_header}
        )

        # Verify the response
        assert response.status_code == 422

        response_text = response.get_data(as_text=True)

        # Verify that the validation error is present in the response
        assert "Invalid date" in response_text

    def test_validation_date_format_http(self, auth_client, db_session):
        """
        Test validation of date format through the HTTP endpoint.
        This tests that dates must be in YYYY-MM-DD format.
        """
        # Define test data with invalid date format
        data = {
            "nombre": "Invalid Date Format Plan",
            "descripcion": "A sales plan with invalid date format",
            "valor_objetivo": 1000.0,
            "fecha_inicio": "01/15/2023",  # MM/DD/YYYY format instead of YYYY-MM-DD
            "fecha_fin": "2023-02-15",
            "seller_ids": [1]
        }

        # Add authorization header for testing
        auth_header = 'Bearer test_token'

        # Make a request to create a sales plan with invalid date format
        response = auth_client.post(
            '/planes',
            json=data,
            headers={'Authorization': auth_header}
        )

        # Verify the response
        assert response.status_code == 422

        response_text = response.get_data(as_text=True)

        # Verify that the validation error is present in the response
        assert "Date must be in the format YYYY-MM-DD" in response_text

    def test_validation_end_date_before_start_date_http(self, auth_client, db_session):
        """
        Test validation that end date must be after start date through the HTTP endpoint.
        This tests the API validation directly.
        """
        # Define test data with end_date before start_date
        today = date.today()
        yesterday = today - timedelta(days=1)

        data = {
            "nombre": "Invalid Sales Plan",
            "descripcion": "A sales plan with invalid dates",
            "valor_objetivo": 1000.0,
            "fecha_inicio": today.strftime('%Y-%m-%d'),
            "fecha_fin": yesterday.strftime('%Y-%m-%d'),
            "seller_ids": [1]
        }

        # Add authorization header for testing
        auth_header = 'Bearer test_token'

        # Make a request to create a sales plan with invalid dates
        response = auth_client.post(
            '/planes',
            json=data,
            headers={'Authorization': auth_header}
        )

        # Verify the response
        # 422 is the standard response code for validation errors in OpenAPI/Pydantic
        assert response.status_code == 422

        response_text = response.get_data(as_text=True)

        # Verify that the validation error is present in the response
        assert "End date must be after start date" in response_text

    def test_get_sales_plan_by_id(self, auth_client, db_session):
        """Test getting a specific sales plan by ID"""
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)

        sales_plan = SalesPlan(
            usuario_id='1',
            nombre="Test Sales Plan",
            descripcion="A test sales plan",
            valor_objetivo=1000.0,
            fecha_inicio=today.strftime('%Y-%m-%d'),
            fecha_fin=end_date.strftime('%Y-%m-%d')
        )

        db_session.add(sales_plan)
        db_session.commit()

        # Get the newly created sales plan's ID
        sales_plan_id = sales_plan.id

        # With our mocked authentication in conftest.py
        auth_header = 'Bearer test_token'

        # Get the sales plan by ID
        response = auth_client.get(
            f'/planes/{sales_plan_id}',
            headers={'Authorization': auth_header}
        )

        assert response.status_code == 200
        assert response.json['nombre'] == "Test Sales Plan"
        assert response.json['descripcion'] == "A test sales plan"
        assert float(response.json['valor_objetivo']) == 1000.0
        assert response.json['usuario_id'] == '1'

    def test_update_sales_plan(self, auth_client, db_session):
        """Test updating a sales plan"""
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)

        sales_plan = SalesPlan(
            usuario_id='1',
            nombre="Original Name",
            descripcion="Original description",
            valor_objetivo=1000.0,
            fecha_inicio=today.strftime('%Y-%m-%d'),
            fecha_fin=end_date.strftime('%Y-%m-%d')
        )

        db_session.add(sales_plan)
        db_session.commit()

        # Get the newly created sales plan's ID
        sales_plan_id = sales_plan.id

        # With our mocked authentication in conftest.py
        auth_header = 'Bearer test_token'

        # Update data
        update_data = {
            "nombre": "Updated Name",
            "descripcion": "Updated description",
            "valor_objetivo": 2000.0
        }

        # Update the sales plan
        response = auth_client.put(
            f'/planes/{sales_plan_id}',
            json=update_data,
            headers={'Authorization': auth_header}
        )

        assert response.status_code == 200
        assert response.json['nombre'] == "Updated Name"
        assert response.json['descripcion'] == "Updated description"
        assert float(response.json['valor_objetivo']) == 2000.0
        assert response.json['usuario_id'] == '1'

        # Verify the update in the database using session.get() as recommended in SQLAlchemy 2.0
        updated_plan = db_session.get(SalesPlan, sales_plan_id)
        assert updated_plan.nombre == "Updated Name"
        assert updated_plan.descripcion == "Updated description"
        assert updated_plan.valor_objetivo == 2000.0
        assert updated_plan.usuario_id == '1'

    def test_delete_sales_plan(self, auth_client, db_session):
        """Test deleting a sales plan"""
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)

        sales_plan = SalesPlan(
            usuario_id='1',
            nombre="To Delete",
            descripcion="A sales plan to delete",
            valor_objetivo=1000.0,
            fecha_inicio=today.strftime('%Y-%m-%d'),
            fecha_fin=end_date.strftime('%Y-%m-%d')
        )

        db_session.add(sales_plan)
        db_session.commit()

        # Get the newly created sales plan's ID
        sales_plan_id = sales_plan.id

        # With our mocked authentication in conftest.py
        auth_header = 'Bearer test_token'

        # Delete the sales plan
        response = auth_client.delete(
            f'/planes/{sales_plan_id}',
            headers={'Authorization': auth_header}
        )

        assert response.status_code == 204

        # Verify the sales plan was deleted from the database using session.get() as recommended in SQLAlchemy 2.0
        deleted_plan = db_session.get(SalesPlan, sales_plan_id)
        assert deleted_plan is None