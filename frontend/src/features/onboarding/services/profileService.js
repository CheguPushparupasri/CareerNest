import { axiosClient } from '../../../lib/axiosClient.js';

export const profileService = {
  get: async () => (await axiosClient.get('/profile/setup')).data.data,
  update: async (payload) => (await axiosClient.put('/profile/setup', payload)).data.data,
  uploadPhoto: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return (await axiosClient.post('/profile/photo', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })).data.data;
  },
  deletePhoto: async () => (await axiosClient.delete('/profile/photo')).data,
};
