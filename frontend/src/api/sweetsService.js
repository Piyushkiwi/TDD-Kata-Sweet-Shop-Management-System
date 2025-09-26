import api from './axiosConfig';

// === User and Admin Functions ===
export const getAllSweets = () => {
  return api.get('/sweets');
};

export const searchSweets = (params) => {
  return api.get('/sweets/search', { params });
};

export const purchaseSweet = (id, quantity) => {
  return api.post(`/sweets/inventory/${id}/purchase`, null, {
    params: { quantity }
  });
};


// === Admin-Only Functions ===
export const createSweet = (sweetData) => {
  return api.post('/sweets', sweetData);
};

export const updateSweet = (id, sweetData) => {
  return api.put(`/sweets/${id}`, sweetData);
};

export const deleteSweet = (id) => {
  return api.delete(`/sweets/${id}`);
};

export const restockSweet = (id, quantity) => {
  return api.post(`/sweets/inventory/${id}/restock`, null, {
    params: { quantity }
  });
};