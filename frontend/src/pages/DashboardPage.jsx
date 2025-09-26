// FINAL AND COMPLETE CODE FOR: src/pages/DashboardPage.jsx

import React, { useState, useEffect } from 'react';
import {
  getAllSweets, searchSweets, purchaseSweet,
  createSweet, updateSweet, deleteSweet, restockSweet
} from '../api/sweetsService';
import { useAuth } from '../contexts/AuthContext';
import Modal from '../components/Modal';

const SweetForm = ({ onSubmit, sweet = {}, buttonLabel }) => {
  const [formData, setFormData] = useState({
    name: sweet.name || '',
    category: sweet.category || '',
    price: sweet.price || '',
    quantity: sweet.quantity || '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
      <input name="name" value={formData.name} onChange={handleChange} placeholder="Name" required style={{ padding: '8px' }}/>
      <input name="category" value={formData.category} onChange={handleChange} placeholder="Category" required style={{ padding: '8px' }}/>
      <input name="price" type="number" step="0.01" value={formData.price} onChange={handleChange} placeholder="Price" required style={{ padding: '8px' }}/>
      <input name="quantity" type="number" value={formData.quantity} onChange={handleChange} placeholder="Quantity" required style={{ padding: '8px' }}/>
      <button type="submit" style={{ padding: '10px', cursor: 'pointer' }}>{buttonLabel}</button>
    </form>
  );
};

function DashboardPage() {
  const [sweets, setSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user, logout, isAdmin } = useAuth();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSweet, setEditingSweet] = useState(null);

  const [searchParams, setSearchParams] = useState({ name: '', category: '' });
  const [purchaseQuantities, setPurchaseQuantities] = useState({});

  useEffect(() => {
    const initialLoad = async () => {
      setLoading(true);
      try {
        const response = await getAllSweets();
        setSweets(response.data);
      } catch (err) {
        setError('Failed to fetch initial sweets list.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    initialLoad();
  }, []);

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchParams(prevParams => ({ ...prevParams, [name]: value }));
  };

  const handleSearchSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const activeSearchParams = {};
      if (searchParams.name) activeSearchParams.name = searchParams.name;
      if (searchParams.category) activeSearchParams.category = searchParams.category;
      
      let response;
      if (Object.keys(activeSearchParams).length > 0) {
        response = await searchSweets(activeSearchParams);
      } else {
        response = await getAllSweets();
      }
      setSweets(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to perform search.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleQuantityChange = (sweetId, quantity) => {
    const numQuantity = Math.max(0, Number(quantity));
    setPurchaseQuantities(prev => ({ ...prev, [sweetId]: numQuantity }));
  };

  const handlePurchase = async (sweetId) => {
    const quantityToPurchase = purchaseQuantities[sweetId] || 0;
    if (quantityToPurchase <= 0) {
      alert("Please enter a quantity greater than zero.");
      return;
    }
    try {
      const response = await purchaseSweet(sweetId, quantityToPurchase);
      const updatedSweet = response.data;
      alert(`Successfully purchased ${quantityToPurchase} of ${updatedSweet.name}!`);
      setSweets(prevSweets => prevSweets.map(sweet => sweet.id === sweetId ? updatedSweet : sweet));
      handleQuantityChange(sweetId, 0);
    } catch (err) {
      alert("Purchase failed. The item may be out of stock.");
      console.error(err);
    }
  };

  const handleFormSubmit = async (sweetData) => {
    try {
      if (editingSweet && editingSweet.id) {
        const updated = await updateSweet(editingSweet.id, sweetData);
        setSweets(sweets.map(s => s.id === editingSweet.id ? updated.data : s));
      } else {
        const created = await createSweet(sweetData);
        setSweets([...sweets, created.data]);
      }
      setIsModalOpen(false);
      setEditingSweet(null);
    } catch (err) {
      alert('Operation failed!');
      console.error(err);
    }
  };

  const handleDelete = async (sweetId) => {
    if (window.confirm('Are you sure you want to delete this sweet?')) {
      try {
        await deleteSweet(sweetId);
        setSweets(sweets.filter(s => s.id !== sweetId));
      } catch (err) {
        alert('Delete failed!');
        console.error(err);
      }
    }
  };

  const handleRestock = async (sweetId) => {
    const quantity = prompt("Enter quantity to restock:", "10");
    if (quantity && !isNaN(quantity) && Number(quantity) > 0) {
      try {
        const updated = await restockSweet(sweetId, Number(quantity));
        setSweets(sweets.map(s => s.id === sweetId ? updated.data : s));
      } catch (err) {
        alert('Restock failed!');
        console.error(err);
      }
    }
  };

  if (loading) return <div>Loading sweets...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>Welcome, {user ? user.email : 'Guest'}!</h2>
        <div>
          {isAdmin() && (
            <button onClick={() => { setEditingSweet(null); setIsModalOpen(true); }} style={{ marginRight: '10px' }}>
              Add New Sweet
            </button>
          )}
          <button onClick={logout}>Logout</button>
        </div>
      </div>

      <div className="search-container" style={{ marginBottom: '20px', padding: '15px', border: '1px solid #eee', borderRadius: '8px' }}>
        <h3>Find Your Favorite Sweet</h3>
        <form onSubmit={handleSearchSubmit}>
          <input type="text" name="name" placeholder="Search by name..." value={searchParams.name} onChange={handleSearchChange} style={{ marginRight: '10px' }}/>
          <input type="text" name="category" placeholder="Search by category..." value={searchParams.category} onChange={handleSearchChange} style={{ marginRight: '10px' }}/>
          <button type="submit">Search</button>
        </form>
      </div>

      <h3>Available Sweets</h3>
      <div className="sweets-list" style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
        {sweets.map((sweet) => (
          <div key={sweet.id} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px', width: '220px', display: 'flex', flexDirection: 'column' }}>
            <div style={{ flexGrow: 1 }}>
              <h4>{sweet.name}</h4>
              <p>Category: {sweet.category}</p>
              <p>Price: ${sweet.price.toFixed(2)}</p>
              <p>In Stock: {sweet.quantity}</p>
            </div>
            
            <div className="purchase-controls" style={{ marginTop: '10px' }}>
              <input type="number" min="0" max={sweet.quantity} value={purchaseQuantities[sweet.id] || 0} onChange={(e) => handleQuantityChange(sweet.id, e.target.value)} style={{ width: '60px', marginRight: '10px' }} disabled={sweet.quantity === 0} />
              <button onClick={() => handlePurchase(sweet.id)} disabled={sweet.quantity === 0 || (purchaseQuantities[sweet.id] || 0) <= 0}>
                Purchase
              </button>
            </div>

            {isAdmin() && (
              <div className="admin-controls" style={{ marginTop: '10px', borderTop: '1px solid #555', paddingTop: '10px' }}>
                <button onClick={() => { setEditingSweet(sweet); setIsModalOpen(true); }} style={{ marginRight: '5px' }}>Edit</button>
                <button onClick={() => handleRestock(sweet.id)} style={{ marginRight: '5px' }}>Restock</button>
                <button onClick={() => handleDelete(sweet.id)}>Delete</button>
              </div>
            )}
          </div>
        ))}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingSweet(null); }}>
        <h2>{editingSweet ? 'Edit Sweet' : 'Add New Sweet'}</h2>
        <SweetForm 
          onSubmit={handleFormSubmit}
          sweet={editingSweet || {}}
          buttonLabel={editingSweet ? 'Update Sweet' : 'Add Sweet'}
        />
      </Modal>
    </div>
  );
}

export default DashboardPage;