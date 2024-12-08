import { Handle, useHandleConnections } from "@xyflow/react";

const CustomHandle = (props: any) => {
    const connections = useHandleConnections({
        type: props.type,
        id: props.branch,
    });
    return (
        <Handle
            {...props}
            isConnectable={props.connectioncount < 0 || connections.length < props.connectioncount}
        />
    );
};

export default CustomHandle;
