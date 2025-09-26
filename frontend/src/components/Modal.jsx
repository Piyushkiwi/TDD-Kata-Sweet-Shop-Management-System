import React from 'react';

// A simple CSS-in-JS for styling our modal
const modalStyles = {
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
  },
  content: {
    background: '#333', // Dark background for the modal content
    color: '#fff', // White text
    padding: '20px',
    borderRadius: '8px',
    position: 'relative',
    width: '90%',
    maxWidth: '500px',
  },
  closeButton: {
    position: 'absolute',
    top: '10px',
    right: '10px',
    background: 'none',
    border: 'none',
    fontSize: '1.5rem',
    cursor: 'pointer',
    color: '#fff',
  }
};

function Modal({ isOpen, onClose, children }) {
  if (!isOpen) {
    return null;
  }

  return (
    <div style={modalStyles.overlay} onClick={onClose}>
      <div style={modalStyles.content} onClick={e => e.stopPropagation()}>
        <button style={modalStyles.closeButton} onClick={onClose}>&times;</button>
        {children}
      </div>
    </div>
  );
}

export default Modal;