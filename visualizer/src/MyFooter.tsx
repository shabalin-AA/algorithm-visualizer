import "./MyFooter.css";

const MyFooter: React.FC = () => {
    return (
        <footer className="footer">
            <div style={{ marginTop: "25px" }}>
                <a href="/doc">Документация</a>
                <a href="/about">О проекте</a>
                <a
                    href="https://github.com/shabalin-aa/algorithm-visualizer"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    GitHub
                </a>
            </div>
            <p>© {new Date().getFullYear()} Все права защищены.</p>
        </footer>
    );
};

export default MyFooter;
