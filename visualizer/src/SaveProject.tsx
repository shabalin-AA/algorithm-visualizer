import React, { useState } from "react";
import "./SaveProject.css"; // Импортируем CSS файл

interface SaveProjectProps {
    onSave: (name: string) => void;
}

const SaveProject: React.FC<SaveProjectProps> = ({ onSave }) => {
    const [isFormVisible, setIsFormVisible] = useState<boolean>(false);
    const [projectName, setProjectName] = useState<string>("");

    const handleSaveClick = () => {
        setIsFormVisible((prev) => !prev); // Переключаем видимость формы
    };

    const handleInputChange = (event: any) => {
        setProjectName(event.target.value);
    };

    const handleSubmit = (event: any) => {
        event.preventDefault();
        onSave(projectName);
        setIsFormVisible(false);
        setProjectName("");
    };

    return (
        <div className="save-project-container">
            <button className="save-button" onClick={handleSaveClick}>
                <span className="save-icon">💾</span>
            </button>
            {isFormVisible && (
                <form onSubmit={handleSubmit} className="save-project-form">
                    <input
                        className="save-project-input"
                        type="text"
                        value={projectName}
                        onChange={handleInputChange}
                        placeholder="Введите название проекта"
                        required
                    />
                    <button type="submit" className="save-project-button">
                        Сохранить
                    </button>
                </form>
            )}
        </div>
    );
};

export default SaveProject;
