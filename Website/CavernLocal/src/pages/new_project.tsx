import { useState } from "react";
import "./form_pages.css"

export async function clientLoader() {
    return {
        title: "New Project",
    }
}
export default function FormComponent({ loaderData }) {
  const [projectName, setProjectName] = useState('');
  const [statusMessage, setStatus] = useState(String);
  const [statusType, setStatusType] = useState(String); // 'success' or 'error'

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('/new_project', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ project_name: projectName }),
      });

      if (!response.ok) throw new Error('API request failed');

      setStatus('Project submitted successfully!');
      setStatusType('success');
    } catch (error) {
      console.error('Submission error:', error);
      setStatus('Error submitting project.');
      setStatusType('error');
    }
  };

  return (
    <div className="form-page">
      <div className="form-heading">
        <h1>{loaderData.title}</h1>
      </div>
      <div className="form-content">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="project_name">Project Name:</label>
            <input
              type="text"
              id="project_name"
              name="project_name"
              value={projectName}
              onChange={(e) => setProjectName(e.target.value)}
            />
          </div>
          <div className="form-submit">
            <input type="submit" value="Submit" />
          </div>
        </form>
        {statusMessage && (
          <p className={`form-status ${statusType}`}>
            {statusMessage}
          </p>
        )}
      </div>
    </div>
  );
}