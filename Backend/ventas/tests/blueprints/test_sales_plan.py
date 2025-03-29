import json
from datetime import date, timedelta
import pytest

from app.models.sales_plan import SalesPlan
from app.models.seller import Seller


class TestSalesPlan:
    def test_create_sales_plan(self, client, db_session):
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
        response = client.post(
            '/sales/sales-plans',
            headers={'Authorization': auth_header},
            json=data
        )
        
        assert response.status_code == 201
        
        # Check that the plan was created in the database
        sales_plan = SalesPlan.query.filter_by(name="Test Sales Plan").first()
        assert sales_plan is not None
        assert sales_plan.description == "A test sales plan"
        assert sales_plan.target_amount == 1000.0
        assert len(sales_plan.sellers) == 1
        assert sales_plan.sellers[0].seller_id == 1
    
    def test_get_sales_plans(self, client, db_session):
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
        response = client.get(
            '/sales/sales-plans',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert len(response.json) == 1
        assert response.json[0]['name'] == "Test Sales Plan"
    
    def test_validation_end_date_before_start_date_direct(self, client, db_session):
        """
        Test validation that end date must be after start date directly against the CreateSalesPlanCommand.
        This tests the business logic directly without going through the HTTP layer.
        """
        from app.commands.sales_plan.create import CreateSalesPlanCommand
        from app.lib.errors import BadRequestError
        
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
            "start_date": today,
            "end_date": yesterday,
            "seller_ids": [1]
        }
        
        # Create the command with invalid data
        command = CreateSalesPlanCommand(data)
        
        # Execute should raise BadRequestError
        with pytest.raises(BadRequestError) as excinfo:
            command.execute()
        
        # Verify the error message
        assert "End date must be after start date" in str(excinfo.value)
        
    def test_get_sales_plan_by_id(self, client, db_session):
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
        response = client.get(
            f'/sales/sales-plans/{sales_plan_id}',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert response.json['name'] == "Test Sales Plan"
        assert response.json['description'] == "A test sales plan"
        assert float(response.json['target_amount']) == 1000.0
        assert len(response.json['sellers']) == 1
    
    def test_update_sales_plan(self, client, db_session):
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
        response = client.put(
            f'/sales/sales-plans/{sales_plan_id}',
            headers={'Authorization': auth_header},
            json=update_data
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
    
    def test_delete_sales_plan(self, client, db_session):
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
        response = client.delete(
            f'/sales/sales-plans/{sales_plan_id}',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 204
        
        # Verify the sales plan was deleted from the database using session.get() as recommended in SQLAlchemy 2.0
        deleted_plan = db_session.get(SalesPlan, sales_plan_id)
        assert deleted_plan is None