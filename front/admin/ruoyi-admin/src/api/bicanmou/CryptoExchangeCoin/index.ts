import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoExchangeCoinVO, CryptoExchangeCoinForm, CryptoExchangeCoinQuery } from '@/api/bicanmou/CryptoExchangeCoin/types';

/**
 * 查询交易所币种关联列表
 * @param query
 * @returns {*}
 */

export const listCryptoExchangeCoin = (query?: CryptoExchangeCoinQuery): AxiosPromise<CryptoExchangeCoinVO[]> => {
  return request({
    url: '/bicanmou/CryptoExchangeCoin/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询交易所币种关联详细
 * @param id
 */
export const getCryptoExchangeCoin = (id: string | number): AxiosPromise<CryptoExchangeCoinVO> => {
  return request({
    url: '/bicanmou/CryptoExchangeCoin/' + id,
    method: 'get'
  });
};

/**
 * 新增交易所币种关联
 * @param data
 */
export const addCryptoExchangeCoin = (data: CryptoExchangeCoinForm) => {
  return request({
    url: '/bicanmou/CryptoExchangeCoin',
    method: 'post',
    data: data
  });
};

/**
 * 修改交易所币种关联
 * @param data
 */
export const updateCryptoExchangeCoin = (data: CryptoExchangeCoinForm) => {
  return request({
    url: '/bicanmou/CryptoExchangeCoin',
    method: 'put',
    data: data
  });
};

/**
 * 删除交易所币种关联
 * @param id
 */
export const delCryptoExchangeCoin = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoExchangeCoin/' + id,
    method: 'delete'
  });
};
