import { Panel } from "@xyflow/react";
import React, { useState } from "react";
import "./SaveProject.css";

interface PlaySavePanelProps {
    execute: () => void;
    halt: () => void;
    save: (name: string) => void;
    isExecuting: () => boolean;
}

const PlaySavePanel: React.FC<PlaySavePanelProps> = ({ execute, halt, isExecuting, save }) => {
    const [isFormVisible, setIsFormVisible] = useState<boolean>(false);
    const [projectName, setProjectName] = useState<string>("");

    const handleSaveClick = () => {
        setIsFormVisible((prev) => !prev);
    };

    const handleInputChange = (event: any) => {
        setProjectName(event.target.value);
    };

    const handleSubmit = (event: any) => {
        event.preventDefault();
        save(projectName);
        setIsFormVisible(false);
        setProjectName("");
    };

    return (
        <Panel style={{ margin: "20px", top: "0px" }} position="top-right">
            <div className="inline-item">
                {isExecuting() ? (
                    <button className="stop-button" onClick={halt}>
                        <span className="stop-icon">■</span>
                    </button>
                ) : (
                    <button className="play-button" onClick={execute}>
                        <span className="play-icon">▶</span>
                    </button>
                )}
            </div>
            <div className="inline-item">
                <button className="save-button" onClick={handleSaveClick}>
                    <span className="save-icon">💾</span>
                </button>
            </div>
            {isFormVisible && (
                <form onSubmit={handleSubmit} className="save-project-form">
                    <input
                        className="save-project-input"
                        type="text"
                        value={projectName}
                        onChange={handleInputChange}
                        placeholder="Название проекта"
                        required
                    />
                    <button type="submit" className="save-project-button">
                        Сохранить
                    </button>
                </form>
            )}
        </Panel>
    );
};

export default PlaySavePanel;
