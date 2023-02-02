import styled from 'styled-components';
import axios from 'axios';
import { useEffect, useState } from 'react';
import DeleteModal from '../Detail/deleteModal';
import Portal from '../../../Components/Function/Portal';
import { useComment } from '../../../Components/API/ReactQueryContainer';

const Box = styled.div`
    display: flex;
    flex-direction: column;
    border-bottom: 1px solid rgb(193, 195, 197);
    padding: 15px 0 15px 0;
    .user {
        display: flex;
        margin-bottom: 10px;
        justify-content: space-between;
    }
    .comment {
        white-space: pre-wrap;
        font-size: 0.9em;
    }
    .nickname {
        font-weight: 600;
        font-size: 1em;
    }
`;
const Li = styled.li`
    list-style: none;
`;

const Button = styled.button`
    width: 40px;
    height: 20px;
    border-radius: 3px;
    margin-left: 5px;
    &:hover {
        background-color: blue;
        color: white;
    }
`;

const Comment = ({ memberId, boardId }) => {
    const url = `${process.env.REACT_APP_API_URL}`;
    const [commentData, setCommentData] = useState([]);
    const comment = useComment(boardId);

    useEffect(() => {
        setCommentData(comment);
    }, [comment]);

    const [modalOn, setModalOn] = useState(false);
    const handleModal = () => {
        setModalOn(!modalOn);
    };

    const deleteHandler = (e) => {
        axios.delete(`${url}/comments/${e}`).then(() => {
            window.location.reload();
        });
    };

    return (
        <>
            {commentData?.map((el, index) => (
                <Li key={index}>
                    <Box>
                        <div className="user">
                            <div className="nickname"> {el.username}</div>
                            {Number(memberId) === Number(el.memberId) ? (
                                <>
                                    <Portal>{modalOn && <DeleteModal forDelete={() => deleteHandler(el.commentId)} onClose={handleModal} />}</Portal>
                                    <Button onClick={() => handleModal()}>삭제</Button>
                                </>
                            ) : (
                                <></>
                            )}
                        </div>
                        <div className="comment">{el.content}</div>
                    </Box>
                </Li>
            ))}
        </>
    );
};
export default Comment;
