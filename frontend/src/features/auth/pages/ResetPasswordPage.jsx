import { Link, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { AuthLayout } from '../../../components/layout/AuthLayout.jsx';
import { Input } from '../../../components/ui/Input.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { authService } from '../services/authService.js';

export function ResetPasswordPage() {
  const [params] = useSearchParams();
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm({ defaultValues: { token: params.get('token') || '' } });

  const onSubmit = async (values) => {
    setError('');
    try {
      await authService.resetPassword(values);
      setMessage('Password reset successful. You can login now.');
    } catch (err) {
      setError(err.response?.data?.message || 'Reset failed');
    }
  };

  return (
    <AuthLayout title="Choose a new password" subtitle="Use a strong password to protect your workspace.">
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        {message ? <Alert type="success">{message}</Alert> : null}
        {error ? <Alert type="error">{error}</Alert> : null}
        <Input label="Reset token" error={errors.token?.message} {...register('token', { required: 'Token is required' })} />
        <Input label="New password" type="password" error={errors.newPassword?.message} {...register('newPassword', { required: 'New password is required', minLength: { value: 8, message: 'Use at least 8 characters' } })} />
        <Input label="Confirm password" type="password" error={errors.confirmPassword?.message} {...register('confirmPassword', { validate: (value) => value === watch('newPassword') || 'Passwords do not match' })} />
        <Button className="w-full" disabled={isSubmitting}>Reset password</Button>
        <Link className="block text-center text-sm font-semibold text-brand-600" to="/login">Back to login</Link>
      </form>
    </AuthLayout>
  );
}
