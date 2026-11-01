import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, Save, Award, FileText } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { resumeService } from '../services/resumeService.js';
import { useAuth } from '../../../app/providers/AuthProvider.jsx';

const emptyDetails = (user) => ({
  format: 'careernest-resume-v1',
  personal: { fullName: user?.fullName || '', email: user?.email || '', phone: '', location: '', headline: '' },
  summary: '',
  education: '',
  skills: '',
  experience: '',
  projects: '',
  certifications: '',
});

function parseResumeContent(content, user) {
  const defaults = emptyDetails(user);
  if (!content) return defaults;
  try {
    const parsed = JSON.parse(content);
    if (parsed && typeof parsed === 'object' && parsed.format === 'careernest-resume-v1') {
      return { ...defaults, ...parsed, personal: { ...defaults.personal, ...parsed.personal } };
    }
  } catch {
    return { ...defaults, summary: content };
  }
  return { ...defaults, summary: content };
}

export function ResumeFormPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const isEdit = !!id;
  
  const [loading, setLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    template: 'MODERN',
    content: '',
    isPrimary: false,
  });
  const [details, setDetails] = useState(() => emptyDetails(user));
  const [error, setError] = useState('');

  const templates = [
    { value: 'MODERN', label: 'Modern', description: 'Clean and contemporary design' },
    { value: 'PROFESSIONAL', label: 'Professional', description: 'Traditional corporate style' },
    { value: 'CREATIVE', label: 'Creative', description: 'Bold and expressive layout' },
    { value: 'MINIMALIST', label: 'Minimalist', description: 'Simple and elegant' },
  ];

  useEffect(() => {
    if (isEdit) {
      loadResume();
    }
  }, [id]);

  const loadResume = async () => {
    try {
      const resume = await resumeService.getById(id);
      setFormData({
        title: resume.title,
        template: resume.template,
        content: resume.content || '',
        isPrimary: resume.isPrimary,
      });
      setDetails(parseResumeContent(resume.content, user));
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load resume.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      const request = { ...formData, content: JSON.stringify(details) };
      if (isEdit) {
        await resumeService.update(id, request);
      } else {
        await resumeService.create(request);
      }
      navigate('/resumes');
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to save resume.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <section>
        <div className="mb-6">
          <button onClick={() => navigate('/resumes')} className="mb-4 flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100">
            <ArrowLeft size={20} />
            Back to Resumes
          </button>
          <h1 className="text-3xl font-bold">Loading...</h1>
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-6 flex items-center gap-4">
        <button
          onClick={() => navigate('/resumes')}
          className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
        >
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-3xl font-bold">{isEdit ? 'Edit Resume' : 'Create New Resume'}</h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {error && <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div>}
        <div className="grid gap-6 lg:grid-cols-2">
          {/* Main Form */}
          <div className="space-y-4">
            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Resume Title *
              </label>
              <input
                type="text"
                required
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                placeholder="e.g., Software Developer Resume"
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Template *
              </label>
              <select
                value={formData.template}
                onChange={(e) => setFormData({ ...formData, template: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
              >
                {templates.map((template) => (
                  <option key={template.value} value={template.value}>
                    {template.label} - {template.description}
                  </option>
                ))}
              </select>
            </div>

            <div className="space-y-4">
              <h2 className="text-lg font-semibold text-slate-950 dark:text-slate-50">Personal information</h2>
              <div className="grid gap-3 sm:grid-cols-2">
                {['fullName', 'email', 'phone', 'location', 'headline'].map((field) => (
                  <label key={field} className={`block space-y-1 text-sm ${field === 'headline' ? 'sm:col-span-2' : ''}`}>
                    <span className="font-medium capitalize text-slate-700 dark:text-slate-300">{field.replace(/([A-Z])/g, ' $1')}</span>
                    <input type={field === 'email' ? 'email' : 'text'} value={details.personal[field]}
                      onChange={(event) => setDetails((current) => ({ ...current, personal: { ...current.personal, [field]: event.target.value } }))}
                      className="w-full rounded-lg border border-slate-300 px-3 py-2 dark:border-slate-700 dark:bg-slate-900" />
                  </label>
                ))}
              </div>
              {[
                ['summary', 'Professional summary'], ['education', 'Education'], ['skills', 'Skills'],
                ['experience', 'Experience'], ['projects', 'Projects'], ['certifications', 'Certifications'],
              ].map(([field, label]) => (
                <label key={field} className="block space-y-1 text-sm">
                  <span className="font-medium text-slate-700 dark:text-slate-300">{label}</span>
                  <textarea rows={field === 'summary' ? 3 : 4} value={details[field]}
                    onChange={(event) => setDetails((current) => ({ ...current, [field]: event.target.value }))}
                    className="w-full rounded-lg border border-slate-300 px-3 py-2 dark:border-slate-700 dark:bg-slate-900"
                    placeholder={`Add ${label.toLowerCase()} details; use one item per line.`} />
                </label>
              ))}
            </div>

            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="isPrimary"
                checked={formData.isPrimary}
                onChange={(e) => setFormData({ ...formData, isPrimary: e.target.checked })}
                className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
              />
              <label htmlFor="isPrimary" className="text-sm text-slate-700 dark:text-slate-300">
                Set as Primary Resume
              </label>
            </div>
          </div>

          {/* ATS Tips */}
          <div className="space-y-4">
            <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-soft dark:border-slate-800 dark:bg-slate-900">
              <h3 className="mb-4 flex items-center gap-2 text-lg font-semibold text-slate-950 dark:text-slate-50">
                <Award size={20} className="text-blue-600" />
                ATS Optimization Tips
              </h3>
              <ul className="space-y-3 text-sm text-slate-600 dark:text-slate-400">
                <li className="flex items-start gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-blue-600" />
                  <span>Use standard section headings (Experience, Education, Skills)</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-blue-600" />
                  <span>Include relevant keywords from job descriptions</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-blue-600" />
                  <span>Avoid complex formatting and graphics</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-blue-600" />
                  <span>Use bullet points for easy scanning</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-blue-600" />
                  <span>Quantify achievements with numbers and percentages</span>
                </li>
                <li className="flex items-start gap-2">
                  <span className="mt-1 h-1.5 w-1.5 rounded-full bg-blue-600" />
                  <span>Include measurable results and outcomes</span>
                </li>
              </ul>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-soft dark:border-slate-800 dark:bg-slate-900">
              <h3 className="mb-4 flex items-center gap-2 text-lg font-semibold text-slate-950 dark:text-slate-50">
                <FileText size={20} className="text-blue-600" />
                Resume Structure Example
              </h3>
              <pre className="overflow-x-auto rounded-lg bg-slate-50 p-4 text-xs dark:bg-slate-800">
{`PROFESSIONAL SUMMARY
Results-driven software developer with 5+ years of experience...

EXPERIENCE
Senior Software Engineer | ABC Company | 2020-Present
• Led development of microservices architecture
• Improved system performance by 40%
• Mentored junior developers

EDUCATION
Bachelor of Computer Science | XYZ University | 2019
• GPA: 3.8/4.0
• Dean's List: All semesters

SKILLS
• Programming: Java, Python, JavaScript
• Frameworks: Spring Boot, React, Node.js
• Databases: MySQL, PostgreSQL, MongoDB

PROJECTS
E-commerce Platform | 2022
• Built full-stack application using React and Spring Boot
• Implemented payment gateway integration
• Achieved 10,000+ monthly users`}
              </pre>
            </div>
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center justify-end gap-3 border-t border-slate-200 pt-4 dark:border-slate-800">
          <button
            type="button"
            onClick={() => navigate('/resumes')}
            className="rounded-lg border border-slate-300 px-4 py-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={submitting}
            className="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700 disabled:opacity-50"
          >
            <Save size={18} />
            {submitting ? 'Saving...' : isEdit ? 'Update Resume' : 'Create Resume'}
          </button>
        </div>
      </form>
    </section>
  );
}
