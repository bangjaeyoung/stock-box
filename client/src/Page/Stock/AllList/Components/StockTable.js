import usePagination from '../../../../Components/Hook/usePagination';
import styled from 'styled-components';
import useCreateTable from '../../../../Components/Hook/useCreateTable';
import { useEffect, useState } from 'react';
import { ascend, descend } from '../../../../Components/Function/sort';
import { SelectBtnContainer } from '../../../../Components/Style/Stock';
import { PageBtn, PageList } from '../../../../Components/Style/PageBtn';
import useStockSearch from '../../../../Components/Hook/useStockSearch';

const IndexBtnContainer = styled.ul`
    display: flex;
    width: 100%;
    list-style: none;
    border-bottom: 1px solid rgb(227 230 232);
    margin: 20px 0px;
    li {
        flex-grow: 1;
        display: flex;
        flex-direction: row;
        justify-content: center;

        .select {
            border-bottom: 2px solid rgb(244 130 37);
        }
    }
    button {
        cursor: pointer;
        padding: 10px;
        width: 100%;
    }
`;

/** 모든 주식정보 페이지에서 주식의 테이블(표)를 만드는 컴포넌트입니다
 * @author 이중원 */
const StockTable = ({ KOSPI, KOSDAQ, keyword, setKeyword }) => {
    const [indexSelect, setIndexSelect] = useState('KOSPI');
    const [sortSelect, setSortSelect] = useState();
    const [currentItems, currentPage, setCurrentPage, pages, renderPageNumbers, handlePrevBtn, handleNextBtn, data, setData] = usePagination(KOSPI);
    const [table, setTable] = useCreateTable();
    const [stock, setWord] = useStockSearch();

    useEffect(() => {
        setWord(keyword);
    }, [keyword]);

    useEffect(() => {
        if (!stock || stock.length === 0) {
            setData(KOSPI);
            setCurrentPage(1);
            return;
        }
        setIndexSelect(null);
        setSortSelect(null);
        setCurrentPage(1);
        setData(stock);
    }, [stock]);

    /** 데이터를 나누는(페이지네이션) usePagination 의 currentItems(현재 출력해야될 데이터들) 값이 변경될때마다 실행되고
     * 랜더링을 담당하는 useCreateTable 에 currentItems을 할당합니다 */
    useEffect(() => {
        if (currentItems?.length !== 0) {
            setTable(currentItems);
        }
    }, [currentItems]);

    /** 클릭한 버튼을 기준으로 코스피와 코스닥 주식정보를 보여줍니다
     * @param {string} select 'KOSPI' or 'KOSDAQ' */
    const handleStockSelect = (select) => {
        if (select === 'KOSDAQ') {
            setIndexSelect('KOSDAQ');
            setData(KOSDAQ);
            setKeyword('');
            setSortSelect(null);
            setCurrentPage(1);
        } else {
            setIndexSelect('KOSPI');
            setData(KOSPI);
            setKeyword('');
            setSortSelect(null);
            setCurrentPage(1);
        }
    };

    /** 정렬버튼을 클릭시 주식정보를 정렬을 해주는 함수입니다
     * 선택된 버튼을 기준으로 정렬을 합니다
     * @param {string} select 정렬할 기준 */
    const handleSortSelect = (select) => {
        setSortSelect(select);
        switch (select) {
            case 'MarketCap':
                setData((current) => descend(current, 'mrktTotAmt'));
                setCurrentPage(1);
                break;
            case 'High':
                setData((current) => descend(current, 'fltRt'));
                setCurrentPage(1);
                break;
            case 'Low':
                setData((current) => ascend(current, 'fltRt'));
                setCurrentPage(1);
                break;
            case 'Volume':
                setData((current) => descend(current, 'trqu'));
                setCurrentPage(1);
                break;
            case 'Amount':
                setData((current) => descend(current, 'trPrc'));
                setCurrentPage(1);
                break;
            default:
                break;
        }
    };

    return (
        <section>
            <SelectBtnContainer>
                <li>
                    <button className={sortSelect === 'MarketCap' ? 'select' : null} onClick={() => handleSortSelect('MarketCap')}>
                        시가총액
                    </button>
                </li>
                <li>
                    <button className={sortSelect === 'High' ? 'select' : null} onClick={() => handleSortSelect('High')}>
                        상승
                    </button>
                </li>
                <li>
                    <button className={sortSelect === 'Low' ? 'select' : null} onClick={() => handleSortSelect('Low')}>
                        하락
                    </button>
                </li>
                <li>
                    <button className={sortSelect === 'Volume' ? 'select' : null} onClick={() => handleSortSelect('Volume')}>
                        거래량
                    </button>
                </li>
                <li>
                    <button className={sortSelect === 'Amount' ? 'select' : null} onClick={() => handleSortSelect('Amount')}>
                        거래대금
                    </button>
                </li>
            </SelectBtnContainer>
            <IndexBtnContainer>
                <li>
                    <button className={indexSelect === 'KOSPI' ? 'select' : null} onClick={() => handleStockSelect('KOSPI')}>
                        코스피
                    </button>
                </li>
                <li>
                    <button className={indexSelect === 'KOSDAQ' ? 'select' : null} onClick={() => handleStockSelect('KOSDAQ')}>
                        코스닥
                    </button>
                </li>
            </IndexBtnContainer>
            {table}
            <PageList>
                <PageBtn onClick={handlePrevBtn} disabled={currentPage === pages[0] ? true : false}>
                    이전
                </PageBtn>
                {renderPageNumbers}
                <PageBtn onClick={handleNextBtn} disabled={currentPage === pages[pages.length - 1] ? true : false}>
                    다음
                </PageBtn>
            </PageList>
        </section>
    );
};
export default StockTable;
