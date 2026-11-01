import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, Save, Upload, X, Star } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { projectService } from '../services/projectService.js';

export function ProjectFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  
  const [loading, setLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    githubUrl: '',
    liveDemoUrl: '',
    techStack: '',
    status: 'IN_PROGRESS',
    startDate: '',
    endDate: '',
    featured: false,
  });
  const [screenshots, setScreenshots] = useState([]);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (isEdit) {
      loadProject();
    }
  }, [id]);

  const loadProject = async () => {
    try {
      const project = await projectService.getById(id);
      setFormData({
        title: project.title,
        description: project.description || '',
        githubUrl: project.githubUrl || '',
        liveDemoUrl: project.liveDemoUrl || '',
        techStack: project.techStack || '',
        status: project.status,
        startDate: project.startDate ? project.startDate.split('T')[0] : '',
        endDate: project.endDate ? project.endDate.split('T')[0] : '',
        featured: project.featured,
      });
      setScreenshots(project.screenshots || []);
    } catch (error) {
      console.error('Failed to load project:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const data = {
        ...formData,
        startDate: formData.startDate ? `${formData.startDate}T00:00:00Z` : null,
        endDate: formData.endDate ? `${formData.endDate}T00:00:00Z` : null,
      };
      
      if (isEdit) {
        await projectService.update(id, data);
      } else {
        await projectService.create(data);
      }
      navigate('/projects');
    } catch (error) {
      console.error('Failed to save project:', error);
      alert('Failed to save project. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    setUploading(true);
    try {
      const projectId = isEdit ? id : null;
      if (projectId) {
        const response = await projectService.uploadScreenshot(projectId, file);
        setScreenshots([...screenshots, response.data.data]);
      } else {
        alert('Please save the project first before uploading screenshots');
      }
    } catch (error) {
      console.error('Failed to upload screenshot:', error);
      alert('Failed to upload screenshot');
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteScreenshot = async (screenshotId) => {
    if (!confirm('Delete this screenshot?')) return;
    try {
      await projectService.deleteScreenshot(id, screenshotId);
      setScreenshots(screenshots.filter(s => s.id !== screenshotId));
    } catch (error) {
      console.error('Failed to delete screenshot:', error);
    }
  };

  if (loading) {
    return (
      <section>
        <div className="mb-6">
          <h1 className="text-3xl font-bold">Loading...</h1>
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-6 flex items-center gap-4">
        <button
          onClick={() => navigate('/projects')}
          className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
        >
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-3xl font-bold">{isEdit ? 'Edit Project' : 'Create New Project'}</h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid gap-6 lg:grid-cols-2">
          {/* Main Form */}
          <div className="space-y-4">
            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Title *
              </label>
              <input
                type="text"
                required
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                placeholder="e.g., E-commerce Platform"
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Description
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                rows={4}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                placeholder="Describe your project, its purpose, and key features..."
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                GitHub URL
              </label>
              <input
                type="url"
                value={formData.githubUrl}
                onChange={(e) => setFormData({ ...formData, githubUrl: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                placeholder="https://github.com/username/repo"
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Live Demo URL
              </label>
              <input
                type="url"
                value={formData.liveDemoUrl}
                onChange={(e) => setFormData({ ...formData, liveDemoUrl: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                placeholder="https://your-project.vercel.app"
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Tech Stack (comma-separated)
              </label>
              <input
                type="text"
                value={formData.techStack}
                onChange={(e) => setFormData({ ...formData, techStack: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                placeholder="React, Node.js, MongoDB, Tailwind CSS"
              />
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                  Start Date
                </label>
                <input
                  type="date"
                  value={formData.startDate}
                  onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                  className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                  End Date
                </label>
                <input
                  type="date"
                  value={formData.endDate}
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                  className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
                />
              </div>
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Status
              </label>
              <select
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                className="w-full rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
              >
                <option value="PLANNED">Planned</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
                <option value="ON_HOLD">On Hold</option>
              </select>
            </div>

            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="featured"
                checked={formData.featured}
                onChange={(e) => setFormData({ ...formData, featured: e.target.checked })}
                className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
              />
              <label htmlFor="featured" className="text-sm text-slate-700 dark:text-slate-300">
                Featured Project
              </label>
            </div>
          </div>

          {/* Screenshots */}
          <div className="space-y-4">
            <div>
              <label className="mb-1.5 block text-sm font-medium text-slate-700 dark:text-slate-300">
                Screenshots
              </label>
              <div className="rounded-lg border-2 border-dashed border-slate-300 p-6 text-center dark:border-slate-700">
                <input
                  type="file"
                  id="screenshot"
                  accept="image/*"
                  onChange={handleImageUpload}
                  className="hidden"
                  disabled={!isEdit || uploading}
                />
                <label
                  htmlFor="screenshot"
                  className={`inline-flex cursor-pointer items-center gap-2 rounded-lg ${
                    !isEdit ? 'bg-slate-100 text-slate-400' : 'bg-blue-600 text-white hover:bg-blue-700'
                  } px-4 py-2 transition-colors`}
                >
                  <Upload size={18} />
                  {uploading ? 'Uploading...' : isEdit ? 'Upload Screenshot' : 'Save project first'}
                </label>
                <p className="mt-2 text-xs text-slate-500">JPEG, PNG, WebP (max 5MB)</p>
              </div>
            </div>

            {screenshots.length > 0 && (
              <div className="space-y-2">
                <p className="text-sm font-medium text-slate-700 dark:text-slate-300">
                  Uploaded Screenshots ({screenshots.length})
                </p>
                <div className="grid gap-2 sm:grid-cols-2">
                  {screenshots.map((screenshot) => (
                    <div key={screenshot.id} className="relative group">
                      <img
                        src={screenshot.imageUrl}
                        alt="Screenshot"
                        className="h-32 w-full rounded-lg object-cover"
                      />
                      <button
                        type="button"
                        onClick={() => handleDeleteScreenshot(screenshot.id)}
                        className="absolute right-2 top-2 rounded-full bg-red-500 p-1.5 text-white opacity-0 transition-opacity group-hover:opacity-100"
                      >
                        <X size={14} />
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center justify-end gap-3 border-t border-slate-200 pt-4 dark:border-slate-800">
          <button
            type="button"
            onClick={() => navigate('/projects')}
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
            {submitting ? 'Saving...' : isEdit ? 'Update Project' : 'Create Project'}
          </button>
        </div>
      </form>
    </section>
  );
}
