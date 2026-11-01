import { axiosClient } from '../../../lib/axiosClient.js';

export const projectService = {
  getAll: async (params = {}) => (await axiosClient.get('/projects', { params })).data.data,
  getById: async (id) => (await axiosClient.get(`/projects/${id}`)).data.data,
  create: async (data) => (await axiosClient.post('/projects', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/projects/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/projects/${id}`),
  toggleFeatured: async (id) => axiosClient.patch(`/projects/${id}/featured`),
  search: async (query, params = {}) => (await axiosClient.get('/projects/search', { params: { ...params, query } })).data.data,
  getByStatus: async (status, params = {}) => (await axiosClient.get(`/projects/status/${status}`, { params })).data.data,
  
  uploadScreenshot: async (projectId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return axiosClient.post(`/projects/${projectId}/screenshots`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  deleteScreenshot: async (projectId, screenshotId) => axiosClient.delete(`/projects/${projectId}/screenshots/${screenshotId}`),
  reorderScreenshots: async (projectId, screenshotIds) => axiosClient.put(`/projects/${projectId}/screenshots/reorder`, screenshotIds),
};
