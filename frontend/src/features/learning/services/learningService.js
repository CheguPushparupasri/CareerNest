import { axiosClient } from '../../../lib/axiosClient.js';

export const learningService = {
  getAll: async (params = {}) => (await axiosClient.get('/learning/courses', { params })).data.data,
  getById: async (id) => (await axiosClient.get(`/learning/courses/${id}`)).data.data,
  create: async (data) => (await axiosClient.post('/learning/courses', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/learning/courses/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/learning/courses/${id}`),
  search: async (query, params = {}) => (await axiosClient.get('/learning/courses/search', { params: { ...params, query } })).data.data,
  getByStatus: async (status, params = {}) => (await axiosClient.get(`/learning/courses/status/${status}`, { params })).data.data,
  count: async () => (await axiosClient.get('/learning/courses/count')).data.data,
};
