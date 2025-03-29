import json
import pytest

from app.models.seller import Seller
from app.lib.database import db


class TestSellers:
    def test_create_seller(self, auth_client, db_session):
        """Test creating a new seller"""
        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'
        
        # Define test seller data
        data = {
            "name": "Test Seller",
            "seller_id": 100
        }
        
        # Create a seller
        response = auth_client.post(
            '/sellers',
            json=data,
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 201
        
        # Check that the seller was created in the database using SQLAlchemy 2.0 style
        seller = db_session.execute(
            db.select(Seller).where(Seller.seller_id == 100)
        ).scalar_one_or_none()
        assert seller is not None
        assert seller.name == "Test Seller"
    
    def test_get_sellers(self, auth_client, db_session):
        """Test getting all sellers"""
        # Create test sellers
        seller1 = Seller(name="Seller 1", seller_id=101)
        seller2 = Seller(name="Seller 2", seller_id=102)
        
        db_session.add(seller1)
        db_session.add(seller2)
        db_session.commit()
        
        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'
        
        # Get all sellers
        response = auth_client.get(
            '/sellers',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert len(response.json['items']) == 2
        
        # Check that the response includes our test sellers
        seller_ids = [seller['seller_id'] for seller in response.json['items']]
        assert 101 in seller_ids
        assert 102 in seller_ids
    
    def test_get_seller_by_id(self, auth_client, db_session):
        """Test getting a specific seller by ID"""
        # Create a test seller
        seller = Seller(name="Specific Seller", seller_id=200)
        db_session.add(seller)
        db_session.commit()
        
        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'
        
        # Get the seller by ID
        response = auth_client.get(
            f'/sellers/200',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        assert response.json['name'] == "Specific Seller"
        assert response.json['seller_id'] == 200
    
    def test_update_seller(self, auth_client, db_session):
        """Test updating a seller"""
        # Create a test seller
        seller = Seller(name="Original Name", seller_id=300)
        db_session.add(seller)
        db_session.commit()
        
        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'
        
        # Update the seller
        data = {
            "name": "Updated Name"
        }
        
        response = auth_client.put(
            f'/sellers/300',
            json=data,
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 200
        
        # Check that the seller was updated in the database using SQLAlchemy 2.0 style
        updated_seller = db_session.execute(
            db.select(Seller).where(Seller.seller_id == 300)
        ).scalar_one_or_none()
        assert updated_seller.name == "Updated Name"
    
    def test_delete_seller(self, auth_client, db_session):
        """Test deleting a seller"""
        # Create a test seller
        seller = Seller(name="To Delete", seller_id=400)
        db_session.add(seller)
        db_session.commit()
        
        # With our mocked authentication in conftest.py, we don't need to get a real token
        auth_header = 'Bearer test_token'
        
        # Delete the seller
        response = auth_client.delete(
            f'/sellers/400',
            headers={'Authorization': auth_header}
        )
        
        assert response.status_code == 204
        
        # Check that the seller was deleted from the database using SQLAlchemy 2.0 style
        deleted_seller = db_session.execute(
            db.select(Seller).where(Seller.seller_id == 400)
        ).scalar_one_or_none()
        assert deleted_seller is None