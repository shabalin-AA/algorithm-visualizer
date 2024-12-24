import { Panel } from "@xyflow/react";
import React, { useState } from "react";
import "./SaveProject.css";

interface PlaySavePanelProps {
    execute: () => void;
    halt: () => void;
    save: (name: string) => void;
    flowchartName: () => string;
    setFlowchartName: (name: string) => void;
    isExecuting: () => boolean;
}

const PlaySavePanel: React.FC<PlaySavePanelProps> = ({
    execute,
    halt,
    save,
    flowchartName,
    setFlowchartName,
    isExecuting,
}) => {
    const [isFormVisible, setIsFormVisible] = useState<boolean>(false);

    const handleSaveClick = () => {
        if (flowchartName() === "") {
            setIsFormVisible((prev) => !prev);
        } else {
            save(flowchartName());
        }
    };

    const handleInputChange = (event: any) => {
        setFlowchartName(event.target.value);
    };

    const handleSubmit = (event: any) => {
        event.preventDefault();
        save(flowchartName());
        setIsFormVisible(false);
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
                        value={flowchartName()}
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
