import { useState } from "react";
import "./EnvironmentVarsPanel.css";
import config from "../config";

interface Props {
  projectId: string;
}

type EnvVar = {
  key: string;
  value: string;
  status: "saved" | "saving" | "error";
};

export default function EnvironmentVarsPanel({ projectId }: Props) {
  const [vars, setVars] = useState<EnvVar[]>([]);
  const [newKey, setNewKey] = useState("");
  const [newValue, setNewValue] = useState("");

  async function addVar() {
    if (!newKey.trim()) return;

    const newVar: EnvVar = {
      key: newKey,
      value: newValue,
      status: "saving",
    };

    setVars(prev => [...prev, newVar]);
    setNewKey("");
    setNewValue("");

    const success = await saveVarsToBackend([...vars, newVar]);

    if (success) {
      setVars(prev =>
        prev.map(v =>
          v.key === newVar.key ? { ...v, status: "saved" } : v
        )
      );
    } else {
      setVars(prev =>
        prev.map(v =>
          v.key === newVar.key ? { ...v, status: "error" } : v
        )
      );

      // Automatically remove failed variable after 3 seconds
      setTimeout(() => {
        setVars(prev => prev.filter(v => v.key !== newVar.key));
      }, 3000);
    }
  }

  async function saveVarsToBackend(varsToSave: EnvVar[]): Promise<boolean> {
    try {
      const res = await fetch(`${config.API_BASE_URL}/projects/${projectId}/env`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(
          varsToSave.map(v => ({
            key: v.key,
            value: v.value,
          }))
        ),
      });

      return res.ok;
    } catch (err) {
      console.error("Failed to save variables:", err);
      return false;
    }
  }

  return (
    <div className="env-panel">
      <h2>Environment Variables</h2>

      <div className="env-form">
        <input
          placeholder="VARIABLE_NAME"
          value={newKey}
          onChange={(e) => setNewKey(e.target.value)}
        />
        <input
          placeholder="value"
          value={newValue}
          onChange={(e) => setNewValue(e.target.value)}
        />
        <button onClick={addVar}>Add</button>
      </div>

      <ul className="env-list">
        {vars.map((v, i) => (
          <li key={i}>
            <strong>{v.key}</strong> = {v.value}{" "}
            {v.status === "saving" && <span style={{ color: "blue" }}>[Saving]</span>}
            {v.status === "saved" && <span style={{ color: "green" }}>[Saved]</span>}
            {v.status === "error" && <span style={{ color: "red" }}>[Error saving]</span>}
          </li>
        ))}
      </ul>
    </div>
  );
}
