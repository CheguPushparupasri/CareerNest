import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../../components/ui/Button.jsx';
import { Input } from '../../../components/ui/Input.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { profileService } from '../services/profileService.js';
import { useAuth } from '../../../app/providers/AuthProvider.jsx';

export function ProfileSetupPage() {
  const navigate = useNavigate();
  const { refreshUser } = useAuth();
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [photo, setPhoto] = useState('');
  const { register, reset, handleSubmit, formState: { errors, isSubmitting } } = useForm();

  useEffect(() => {
    profileService.get().then((profile) => {
      reset(profile);
      setPhoto(profile.profilePhotoUrl);
    });
  }, [reset]);

  const onSubmit = async (values) => {
    setError('');
    try {
      await profileService.update(values);
      await refreshUser();
      setMessage('Profile setup saved');
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to save profile');
    }
  };

  const uploadPhoto = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    try {
      const profile = await profileService.uploadPhoto(file);
      setPhoto(profile.profilePhotoUrl);
    } catch (err) {
      setError(err.response?.data?.message || 'Photo upload failed');
    }
  };

  return (
    <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-soft dark:border-slate-800 dark:bg-slate-900">
      <div className="mb-6">
        <h1 className="text-2xl font-bold">Profile setup</h1>
        <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">Complete the basics so CareerNest can personalize your workspace.</p>
      </div>
      <form className="grid gap-4 lg:grid-cols-2" onSubmit={handleSubmit(onSubmit)}>
        {message ? <div className="lg:col-span-2"><Alert type="success">{message}</Alert></div> : null}
        {error ? <div className="lg:col-span-2"><Alert type="error">{error}</Alert></div> : null}
        <div className="lg:col-span-2 flex items-center gap-4">
          <div className="h-20 w-20 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
            {photo ? <img src={photo} alt="Profile" className="h-full w-full object-cover" /> : null}
          </div>
          <label className="inline-flex min-h-11 cursor-pointer items-center rounded-lg bg-slate-100 px-4 text-sm font-semibold text-slate-900 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-100">
            Upload photo
            <input type="file" accept="image/png,image/jpeg,image/webp" className="hidden" onChange={uploadPhoto} />
          </label>
        </div>
        <Input label="Headline" error={errors.headline?.message} {...register('headline', { required: 'Headline is required' })} />
        <Input label="Phone" {...register('phone')} />
        <Input label="Location" error={errors.location?.message} {...register('location', { required: 'Location is required' })} />
        <Input label="Education summary" error={errors.educationSummary?.message} {...register('educationSummary', { required: 'Education summary is required' })} />
        <Input label="Portfolio URL" {...register('portfolioUrl')} />
        <Input label="LinkedIn URL" {...register('linkedinUrl')} />
        <Input label="GitHub URL" {...register('githubUrl')} />
        <div className="lg:col-span-2 flex justify-end">
          <Button disabled={isSubmitting}>{isSubmitting ? 'Saving...' : 'Save profile'}</Button>
        </div>
      </form>
    </section>
  );
}
