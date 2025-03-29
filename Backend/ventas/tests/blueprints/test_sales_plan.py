import json
from datetime import date, timedelta
import pytest

from app.models.sales_plan import SalesPlan
from app.models.seller import Seller
from app.lib.database import db


class TestSalesPlan:
    def test_create_sales_plan(self, auth_client, db_session):
        # Create a test seller
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        db_session.commit()
        
        # With our mocked authentication in conftest.py, we don't need to get a real token
        # We'll add a dummy header that will be ignored
        auth_header = 'Bearer test_token'
        
        # Define test sales plan data
        today = date.today()
        end_date = today + timedelta(days=30)
        
        data = {
            "name": "Test Sales Plan",
            "description": "A test sales plan",
            "target_amount": 1000.0,
            "start_date": today.strftime('%Y-%m-%d'),
            "end_date": end_date.strftime('%Y-%m-%d'),
            "seller_ids": [1]
        }
        
        # Create a sales plan
        response = auth_client.post(
            '/sales-plans',
            json=data,
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 201
        
        # Check that the plan was created in the database using SQLAlchemy 2.0 style
        sales_plan = db_session.execute(
            db.select(SalesPlan).where(SalesPlan.name == "Test Sales Plan")
        ).scalar_one_or_none()
        assert sales_plan is not None
        assert sales_plan.description == "A test sales plan"
        assert sales_plan.target_amount == 1000.0
        assert len(sales_plan.sellers) == 1
        assert sales_plan.sellers[0].seller_id == 1
    
    def test_get_sales_plans(self, auth_client, db_session):
        # Create a test seller
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)
        
        sales_plan = SalesPlan(
            name="Test Sales Plan",
            description="A test sales plan",
            target_amount=1000.0,
            start_date=today,
            end_date=end_date
        )
        sales_plan.sellers.append(seller)
        
        db_session.add(sales_plan)
        db_session.commit()
        
        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'
        
        # Get all sales plans
        response = auth_client.get(
            '/sales-plans',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert len(response.json['items']) == 1
        assert response.json['items'][0]['name'] == "Test Sales Plan"
    
    def test_validation_invalid_date_http(self, auth_client, db_session):
        """
        Test validation of invalid dates through the HTTP endpoint.
        This tests that dates must be valid calendar dates.
        """
        # Create a test seller for the plan
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        db_session.commit()
        
        # Define test data with invalid calendar date
        data = {
            "name": "Invalid Calendar Date Plan",
            "description": "A sales plan with invalid calendar date",
            "target_amount": 1000.0,
            "start_date": "2023-02-30",  # February 30th doesn't exist
            "end_date": "2023-03-15",
            "seller_ids": [1]
        }
        
        # Add authorization header for testing
        auth_header = 'Bearer test_token'
        
        # Make a request to create a sales plan with invalid calendar date
        response = auth_client.post(
            '/sales-plans',
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
        # Create a test seller for the plan
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        db_session.commit()
        
        # Define test data with invalid date format
        data = {
            "name": "Invalid Date Format Plan",
            "description": "A sales plan with invalid date format",
            "target_amount": 1000.0,
            "start_date": "01/15/2023",  # MM/DD/YYYY format instead of YYYY-MM-DD
            "end_date": "2023-02-15",
            "seller_ids": [1]
        }
        
        # Add authorization header for testing
        auth_header = 'Bearer test_token'
        
        # Make a request to create a sales plan with invalid date format
        response = auth_client.post(
            '/sales-plans',
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
        # Create a test seller for the plan
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        db_session.commit()
        
        # Define test data with end_date before start_date
        today = date.today()
        yesterday = today - timedelta(days=1)
        
        data = {
            "name": "Invalid Sales Plan",
            "description": "A sales plan with invalid dates",
            "target_amount": 1000.0,
            "start_date": today.strftime('%Y-%m-%d'),
            "end_date": yesterday.strftime('%Y-%m-%d'),
            "seller_ids": [1]
        }
        
        # Add authorization header for testing
        auth_header = 'Bearer test_token'
        
        # Make a request to create a sales plan with invalid dates
        response = auth_client.post(
            '/sales-plans',
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
        # Create a test seller
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)
        
        sales_plan = SalesPlan(
            name="Test Sales Plan",
            description="A test sales plan",
            target_amount=1000.0,
            start_date=today,
            end_date=end_date
        )
        sales_plan.sellers.append(seller)
        
        db_session.add(sales_plan)
        db_session.commit()
        
        # Get the newly created sales plan's ID
        sales_plan_id = sales_plan.id
        
        # With our mocked authentication in conftest.py
        auth_header = 'Bearer test_token'
        
        # Get the sales plan by ID
        response = auth_client.get(
            f'/sales-plans/{sales_plan_id}',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert response.json['name'] == "Test Sales Plan"
        assert response.json['description'] == "A test sales plan"
        assert float(response.json['target_amount']) == 1000.0
        assert len(response.json['sellers']) == 1
    
    def test_update_sales_plan(self, auth_client, db_session):
        """Test updating a sales plan"""
        # Create a test seller
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)
        
        sales_plan = SalesPlan(
            name="Original Name",
            description="Original description",
            target_amount=1000.0,
            start_date=today,
            end_date=end_date
        )
        sales_plan.sellers.append(seller)
        
        db_session.add(sales_plan)
        db_session.commit()
        
        # Get the newly created sales plan's ID
        sales_plan_id = sales_plan.id
        
        # With our mocked authentication in conftest.py
        auth_header = 'Bearer test_token'
        
        # Update data
        update_data = {
            "name": "Updated Name",
            "description": "Updated description",
            "target_amount": 2000.0,
            "seller_ids": [1]
        }
        
        # Update the sales plan
        response = auth_client.put(
            f'/sales-plans/{sales_plan_id}',
            json=update_data,
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert response.json['name'] == "Updated Name"
        assert response.json['description'] == "Updated description"
        assert float(response.json['target_amount']) == 2000.0
        
        # Verify the update in the database using session.get() as recommended in SQLAlchemy 2.0
        updated_plan = db_session.get(SalesPlan, sales_plan_id)
        assert updated_plan.name == "Updated Name"
        assert updated_plan.description == "Updated description"
        assert updated_plan.target_amount == 2000.0
    
    def test_delete_sales_plan(self, auth_client, db_session):
        """Test deleting a sales plan"""
        # Create a test seller
        seller = Seller(
            name="Test Seller",
            seller_id=1
        )
        db_session.add(seller)
        
        # Create a test sales plan
        today = date.today()
        end_date = today + timedelta(days=30)
        
        sales_plan = SalesPlan(
            name="To Delete",
            description="A sales plan to delete",
            target_amount=1000.0,
            start_date=today,
            end_date=end_date
        )
        sales_plan.sellers.append(seller)
        
        db_session.add(sales_plan)
        db_session.commit()
        
        # Get the newly created sales plan's ID
        sales_plan_id = sales_plan.id
        
        # With our mocked authentication in conftest.py
        auth_header = 'Bearer test_token'
        
        # Delete the sales plan
        response = auth_client.delete(
            f'/sales-plans/{sales_plan_id}',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 204
        
        # Verify the sales plan was deleted from the database using session.get() as recommended in SQLAlchemy 2.0
        deleted_plan = db_session.get(SalesPlan, sales_plan_id)
        assert deleted_plan is None