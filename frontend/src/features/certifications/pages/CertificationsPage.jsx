import { useEffect, useMemo, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, Award, Search, Filter, Trash2, Edit, ExternalLink } from 'lucide-react';
import { certificationService } from '../services/certificationService.js';

export function CertificationsPage() {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({ title: '', issuer: '', category: '', description: '', credentialUrl: '', imageUrl: '', issueDate: '', expiryDate: '', verified: false });

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await certificationService.getAll({ page: 0, size: 20 });
      setItems(data.content || []);
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load certifications.');
    } finally {
      setLoading(false);
    }
  };

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const haystack = `${item.title} ${item.issuer} ${item.category}`.toLowerCase();
      const matchesQuery = !query || haystack.includes(query.toLowerCase());
      const matchesCategory = !category || item.category === category;
      return matchesQuery && matchesCategory;
    });
  }, [items, query, category]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    const toInstant = (value) => value ? `${value}T00:00:00Z` : '';
    const payload = { ...form, issueDate: toInstant(form.issueDate), expiryDate: toInstant(form.expiryDate) };
    try {
      if (editingId) await certificationService.update(editingId, payload);
      else await certificationService.create(payload);
      setMessage(editingId ? 'Certification updated.' : 'Certification added.');
      setEditingId(null);
      setForm({ title: '', issuer: '', category: '', description: '', credentialUrl: '', imageUrl: '', issueDate: '', expiryDate: '', verified: false });
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to save certification.');
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm({ ...item,
      issueDate: item.issueDate ? new Date(item.issueDate).toISOString().slice(0, 10) : '',
      expiryDate: item.expiryDate ? new Date(item.expiryDate).toISOString().slice(0, 10) : '',
    });
    setError('');
    setMessage('');
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this certification?')) return;
    try {
      await certificationService.delete(id);
      if (editingId === id) {
        setEditingId(null);
        setForm({ title: '', issuer: '', category: '', description: '', credentialUrl: '', imageUrl: '', issueDate: '', expiryDate: '', verified: false });
      }
      setMessage('Certification deleted.');
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to delete certification.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <h1 className="text-3xl font-bold">Certifications</h1>
          <p className="mt-1 text-slate-500">Track credentials, categories, verification status, and renewal dates.</p>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
          {error && <p role="alert" className="mb-3 text-sm text-red-600">{error}</p>}
          {message && <p role="status" className="mb-3 text-sm text-green-700">{message}</p>}
          <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
            <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
            <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Issuer" value={form.issuer} onChange={(e) => setForm({ ...form, issuer: e.target.value })} required />
            <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Category" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} required />
            <input className="rounded-lg border border-slate-300 px-3 py-2 md:col-span-2 xl:col-span-3" placeholder="Credential URL" value={form.credentialUrl} onChange={(e) => setForm({ ...form, credentialUrl: e.target.value })} />
            <textarea className="rounded-lg border border-slate-300 px-3 py-2 md:col-span-2 xl:col-span-3" rows="3" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            <label className="space-y-1 text-sm">Issue date<input type="date" className="block w-full rounded-lg border border-slate-300 px-3 py-2" value={form.issueDate} onChange={(e) => setForm({ ...form, issueDate: e.target.value })} /></label>
            <label className="space-y-1 text-sm">Expiry date<input type="date" className="block w-full rounded-lg border border-slate-300 px-3 py-2" value={form.expiryDate} onChange={(e) => setForm({ ...form, expiryDate: e.target.value })} /></label>
            <label className="flex items-center gap-2 text-sm text-slate-600">
              <input type="checkbox" checked={form.verified} onChange={(e) => setForm({ ...form, verified: e.target.checked })} />
              Verified
            </label>
            <div className="md:col-span-2 xl:col-span-3 flex justify-end">
              <button type="submit" className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-white">
                {editingId ? <Edit size={16} /> : <Plus size={16} />} {editingId ? 'Save changes' : 'Add Certification'}
              </button>
              {editingId && <button type="button" className="rounded-lg border px-4 py-2" onClick={() => { setEditingId(null); setForm({ title: '', issuer: '', category: '', description: '', credentialUrl: '', imageUrl: '', issueDate: '', expiryDate: '', verified: false }); }}>Cancel edit</button>}
            </div>
          </form>
        </div>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="flex gap-2">
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Search size={16} />
              <input className="bg-transparent outline-none" placeholder="Search" value={query} onChange={(e) => setQuery(e.target.value)} />
            </div>
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Filter size={16} />
              <select className="bg-transparent outline-none" value={category} onChange={(e) => setCategory(e.target.value)}>
                <option value="">All categories</option>
                {[...new Set(items.map((item) => item.category).filter(Boolean))].map((value) => (
                  <option key={value} value={value}>{value}</option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {loading ? (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {[1, 2, 3].map((i) => <div key={i} className="h-36 animate-pulse rounded-xl bg-slate-200" />)}
          </div>
        ) : filteredItems.length === 0 ? (
          <div className="rounded-xl border border-dashed border-slate-300 p-8 text-center text-slate-500">No certifications found.</div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {filteredItems.map((item) => (
              <motion.div key={item.id} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="rounded-xl border border-slate-200 p-4 dark:border-slate-800">
                <div className="mb-3 flex items-start justify-between gap-2">
                  <div>
                    <h3 className="font-semibold">{item.title}</h3>
                    <p className="text-sm text-slate-500">{item.issuer}</p>
                  </div>
                  {item.verified ? <span className="rounded-full bg-green-100 px-2 py-1 text-xs text-green-700">Verified</span> : null}
                </div>
                <p className="mb-3 text-sm text-slate-600">{item.category}</p>
                <p className="mb-3 text-sm text-slate-500">{item.description}</p>
                <div className="flex items-center gap-2 text-xs text-slate-500">
                  <Award size={14} />
                  <span>{item.issueDate ? new Date(item.issueDate).toLocaleDateString() : 'No issue date'}</span>
                </div>
                <div className="mt-4 flex items-center justify-end gap-2">
                  {item.credentialUrl ? <a href={item.credentialUrl} target="_blank" rel="noreferrer" aria-label="Open credential" className="rounded-lg bg-slate-100 p-2"><ExternalLink size={16} /></a> : null}
                  <button type="button" aria-label={`Edit ${item.title}`} className="rounded-lg bg-slate-100 p-2" onClick={() => startEdit(item)}><Edit size={16} /></button>
                  <button className="rounded-lg bg-slate-100 p-2" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
