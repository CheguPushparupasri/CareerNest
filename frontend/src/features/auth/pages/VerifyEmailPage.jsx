import { Link, useSearchParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { AuthLayout } from '../../../components/layout/AuthLayout.jsx';
import { Alert } from '../../../components/ui/Alert.jsx';
import { authService } from '../services/authService.js';

export function VerifyEmailPage() {
  const [params] = useSearchParams();
  const [state, setState] = useState({ loading: true, error: '', message: '' });

  useEffect(() => {
    const token = params.get('token');
    if (!token) {
      setState({ loading: false, error: 'Verification token is missing', message: '' });
      return;
    }
    authService.verifyEmail(token)
      .then(() => setState({ loading: false, error: '', message: 'Email verified. You can login now.' }))
      .catch((err) => setState({ loading: false, error: err.response?.data?.message || 'Verification failed', message: '' }));
  }, [params]);

  return (
    <AuthLayout title="Email verification" subtitle="Activating your CareerNest account.">
      <div className="space-y-4">
        {state.loading ? <Alert>Verifying email...</Alert> : null}
        {state.message ? <Alert type="success">{state.message}</Alert> : null}
        {state.error ? <Alert type="error">{state.error}</Alert> : null}
        <Link className="block text-center text-sm font-semibold text-brand-600" to="/login">Go to login</Link>
      </div>
    </AuthLayout>
  );
}
