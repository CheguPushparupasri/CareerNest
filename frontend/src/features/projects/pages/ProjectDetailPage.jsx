import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, Edit, Trash2, Star, Github, ExternalLink, Calendar, Tag, Image as ImageIcon } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { projectService } from '../services/projectService.js';

export function ProjectDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProject();
  }, [id]);

  const loadProject = async () => {
    try {
      const data = await projectService.getById(id);
      setProject(data);
    } catch (error) {
      console.error('Failed to load project:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm(`Are you sure you want to delete "${project.title}"?`)) return;
    try {
      await projectService.delete(id);
      navigate('/projects');
    } catch (error) {
      console.error('Failed to delete project:', error);
    }
  };

  const handleToggleFeatured = async () => {
    try {
      await projectService.toggleFeatured(id);
      loadProject();
    } catch (error) {
      console.error('Failed to toggle featured:', error);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-700 dark:bg-green-950 dark:text-green-300';
      case 'IN_PROGRESS': return 'bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300';
      case 'PLANNED': return 'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300';
      case 'ON_HOLD': return 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300';
      default: return 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300';
    }
  };

  if (loading) {
    return (
      <section>
        <div className="mb-6">
          <button onClick={() => navigate('/projects')} className="mb-4 flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100">
            <ArrowLeft size={20} />
            Back to Projects
          </button>
          <h1 className="text-3xl font-bold">Loading...</h1>
        </div>
      </section>
    );
  }

  if (!project) {
    return (
      <section>
        <div className="mb-6">
          <button onClick={() => navigate('/projects')} className="mb-4 flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100">
            <ArrowLeft size={20} />
            Back to Projects
          </button>
          <h1 className="text-3xl font-bold">Project not found</h1>
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-6 flex items-center justify-between">
        <button
          onClick={() => navigate('/projects')}
          className="flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100"
        >
          <ArrowLeft size={20} />
          Back to Projects
        </button>
        <div className="flex items-center gap-2">
          <button
            onClick={handleToggleFeatured}
            className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
          >
            <Star size={18} className={project.featured ? 'fill-amber-400 text-amber-400' : ''} />
          </button>
          <button
            onClick={() => navigate(`/projects/${id}/edit`)}
            className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
          >
            <Edit size={18} />
          </button>
          <button
            onClick={handleDelete}
            className="rounded-lg border border-red-300 p-2 text-red-600 transition-colors hover:bg-red-50 dark:border-red-900 dark:text-red-400 dark:hover:bg-red-950/30"
          >
            <Trash2 size={18} />
          </button>
        </div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="space-y-6"
      >
        {/* Header */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center gap-3">
                <h1 className="text-2xl font-bold text-slate-950 dark:text-slate-50">{project.title}</h1>
                {project.featured && <Star className="fill-amber-400 text-amber-400" size={20} />}
              </div>
              <span className={`mt-2 inline-block rounded-full px-3 py-1 text-xs font-medium ${getStatusColor(project.status)}`}>
                {project.status.replace('_', ' ')}
              </span>
            </div>
            <div className="flex gap-2">
              {project.githubUrl && (
                <a
                  href={project.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-2 rounded-lg border border-slate-300 px-4 py-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
                >
                  <Github size={18} />
                  GitHub
                </a>
              )}
              {project.liveDemoUrl && (
                <a
                  href={project.liveDemoUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
                >
                  <ExternalLink size={18} />
                  Live Demo
                </a>
              )}
            </div>
          </div>

          {project.description && (
            <p className="mt-4 text-slate-600 dark:text-slate-400">{project.description}</p>
          )}

          {project.techStack && (
            <div className="mt-4 flex flex-wrap gap-2">
              {project.techStack.split(',').map((tech, i) => (
                <span
                  key={i}
                  className="flex items-center gap-1.5 rounded-full bg-slate-100 px-3 py-1 text-sm text-slate-600 dark:bg-slate-800 dark:text-slate-400"
                >
                  <Tag size={14} />
                  {tech.trim()}
                </span>
              ))}
            </div>
          )}

          <div className="mt-4 flex gap-6 text-sm text-slate-500">
            {project.startDate && (
              <div className="flex items-center gap-2">
                <Calendar size={16} />
                <span>Started: {new Date(project.startDate).toLocaleDateString()}</span>
              </div>
            )}
            {project.endDate && (
              <div className="flex items-center gap-2">
                <Calendar size={16} />
                <span>Completed: {new Date(project.endDate).toLocaleDateString()}</span>
              </div>
            )}
          </div>
        </div>

        {/* Screenshots */}
        {project.screenshots && project.screenshots.length > 0 && (
          <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
            <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold text-slate-950 dark:text-slate-50">
              <ImageIcon size={20} />
              Screenshots ({project.screenshots.length})
            </h2>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {project.screenshots.map((screenshot) => (
                <motion.div
                  key={screenshot.id}
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="overflow-hidden rounded-lg border border-slate-200 dark:border-slate-800"
                >
                  <img
                    src={screenshot.imageUrl}
                    alt="Project screenshot"
                    className="h-48 w-full object-cover"
                  />
                </motion.div>
              ))}
            </div>
          </div>
        )}
      </motion.div>
    </section>
  );
}
