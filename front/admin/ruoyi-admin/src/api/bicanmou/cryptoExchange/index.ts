import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoExchangeVO, CryptoExchangeForm, CryptoExchangeQuery } from '@/api/bicanmou/cryptoExchange/types';

/**
 * 查询交易所信息列表
 * @param query
 * @returns {*}
 */

export const listCryptoExchange = (query?: CryptoExchangeQuery): AxiosPromise<CryptoExchangeVO[]> => {
  return request({
    url: '/bicanmou/cryptoExchange/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询交易所信息详细
 * @param id
 */
export const getCryptoExchange = (id: string | number): AxiosPromise<CryptoExchangeVO> => {
  return request({
    url: '/bicanmou/cryptoExchange/' + id,
    method: 'get'
  });
};

/**
 * 新增交易所信息
 * @param data
 */
export const addCryptoExchange = (data: CryptoExchangeForm) => {
  return request({
    url: '/bicanmou/cryptoExchange',
    method: 'post',
    data: data
  });
};

/**
 * 修改交易所信息
 * @param data
 */
export const updateCryptoExchange = (data: CryptoExchangeForm) => {
  return request({
    url: '/bicanmou/cryptoExchange',
    method: 'put',
    data: data
  });
};

/**
 * 删除交易所信息
 * @param id
 */
export const delCryptoExchange = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/cryptoExchange/' + id,
    method: 'delete'
  });
};
