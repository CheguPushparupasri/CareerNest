import React from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { router } from './app/router.jsx';
import { AuthProvider } from './app/providers/AuthProvider.jsx';
import { ThemeProvider } from './app/providers/ThemeProvider.jsx';
import './styles/globals.css';

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ThemeProvider>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>,
);
