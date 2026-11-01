import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { AuthLayout } from '../../../components/layout/AuthLayout.jsx';
import { Input } from '../../../components/ui/Input.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { authService } from '../services/authService.js';

export function ForgotPasswordPage() {
  const [message, setMessage] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();

  const onSubmit = async (values) => {
    await authService.forgotPassword(values);
    setMessage('If the email exists, a reset link has been sent. In local development, check backend logs.');
  };

  return (
    <AuthLayout title="Reset your password" subtitle="We will send reset instructions if the account exists.">
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        {message ? <Alert type="success">{message}</Alert> : null}
        <Input label="Email" type="email" error={errors.email?.message} {...register('email', { required: 'Email is required' })} />
        <Button className="w-full" disabled={isSubmitting}>{isSubmitting ? 'Sending...' : 'Send reset link'}</Button>
        <Link className="block text-center text-sm font-semibold text-brand-600" to="/login">Back to login</Link>
      </form>
    </AuthLayout>
  );
}
