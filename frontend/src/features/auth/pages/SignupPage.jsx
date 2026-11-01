import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { AuthLayout } from '../../../components/layout/AuthLayout.jsx';
import { Input } from '../../../components/ui/Input.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { authService } from '../services/authService.js';

export function SignupPage() {
  const navigate = useNavigate();
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm();

  const onSubmit = async (values) => {
    setError('');
    setMessage('');
    try {
      const response = await authService.signup(values);
      setMessage(response.message);
      setTimeout(() => navigate('/login'), 1400);
    } catch (err) {
      setError(err.response?.data?.message || 'Signup failed');
    }
  };

  return (
    <AuthLayout title="Create your account" subtitle="Start building your career workspace with a premium experience.">
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        {message ? <Alert type="success">{message}</Alert> : null}
        {error ? <Alert type="error">{error}</Alert> : null}
        <Input label="Full name" error={errors.fullName?.message} {...register('fullName', { required: 'Full name is required', minLength: { value: 2, message: 'Enter at least 2 characters' } })} />
        <Input label="Email" type="email" error={errors.email?.message} {...register('email', { required: 'Email is required' })} />
        <Input label="Password" type="password" error={errors.password?.message} {...register('password', { required: 'Password is required', minLength: { value: 8, message: 'Use at least 8 characters' } })} />
        <Input label="Confirm password" type="password" error={errors.confirmPassword?.message} {...register('confirmPassword', { validate: (value) => value === watch('password') || 'Passwords do not match' })} />
        <Button className="w-full" disabled={isSubmitting}>{isSubmitting ? 'Creating...' : 'Create account'}</Button>
        <p className="text-center text-sm text-slate-500">Already have an account? <Link className="font-semibold text-brand-600" to="/login">Login</Link></p>
      </form>
    </AuthLayout>
  );
}
