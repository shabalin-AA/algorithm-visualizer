export interface NewNodeMenuProps {
    visible: boolean;
    position: { x: number; y: number };
    newNode: (type: string, x: number, y: number) => void;
}

const NewNodeMenu: React.FC<NewNodeMenuProps> = ({ visible, position, newNode }) => {
    return (
        <ul
            style={{
                position: "absolute",
                top: position.y,
                left: position.x,
                backgroundColor: "white",
                border: "1px solid black",
                listStyleType: "none",
                padding: "10px",
                margin: 0,
            }}
        >
            <li onClick={(e) => newNode("NodeCalc", e.clientX, e.clientY)}>узел расчета</li>
            <li onClick={(e) => newNode("NodeIf", e.clientX, e.clientY)}>узел ветвления</li>
        </ul>
    );
};

export default NewNodeMenu;
