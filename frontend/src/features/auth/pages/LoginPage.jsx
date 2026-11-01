import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { AuthLayout } from '../../../components/layout/AuthLayout.jsx';
import { Input } from '../../../components/ui/Input.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { useAuth } from '../../../app/providers/AuthProvider.jsx';

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();

  const onSubmit = async (values) => {
    setError('');
    try {
      const user = await login(values);
      navigate('/role-selection');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    }
  };

  return (
    <AuthLayout title="Welcome back" subtitle="Log in to continue building your career workspace.">
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        {error ? <Alert type="error">{error}</Alert> : null}
        <Input label="Email" type="email" error={errors.email?.message} {...register('email', { required: 'Email is required' })} />
        <Input label="Password" type="password" error={errors.password?.message} {...register('password', { required: 'Password is required' })} />
        <div className="text-right"><Link className="text-sm font-semibold text-brand-600" to="/forgot-password">Forgot password?</Link></div>
        <Button className="w-full" disabled={isSubmitting}>{isSubmitting ? 'Logging in...' : 'Login'}</Button>
        <p className="text-center text-sm text-slate-500">New to CareerNest? <Link className="font-semibold text-brand-600" to="/signup">Create account</Link></p>
      </form>
    </AuthLayout>
  );
}
