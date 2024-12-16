import "./TopBar.css";
import { NavLink } from "react-router-dom"; // Импортируем Link для навигации

const TopBar = () => {
    return (
        <div className="topbar">
            <NavLink to="/" className="topbar-item">
                Редактор
            </NavLink>
            <NavLink to="/doc" className="topbar-item">
                Документация
            </NavLink>
            <NavLink to="/about" className="topbar-item">
                О проекте
            </NavLink>
            <a
                href="https://github.com/shabalin-aa/algorithm-visualizer"
                target="_blank"
                rel="noopener noreferrer"
                className="topbar-item"
            >
                GitHub
            </a>
        </div>
    );
};
export default TopBar;
