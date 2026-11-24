import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoCoinVO, CryptoCoinForm, CryptoCoinQuery } from '@/api/bicanmou/CryptoCoin/types';

/**
 * 查询币种信息列表
 * @param query
 * @returns {*}
 */

export const listCryptoCoin = (query?: CryptoCoinQuery): AxiosPromise<CryptoCoinVO[]> => {
  return request({
    url: '/bicanmou/CryptoCoin/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询币种信息详细
 * @param id
 */
export const getCryptoCoin = (id: string | number): AxiosPromise<CryptoCoinVO> => {
  return request({
    url: '/bicanmou/CryptoCoin/' + id,
    method: 'get'
  });
};

/**
 * 新增币种信息
 * @param data
 */
export const addCryptoCoin = (data: CryptoCoinForm) => {
  return request({
    url: '/bicanmou/CryptoCoin',
    method: 'post',
    data: data
  });
};

/**
 * 修改币种信息
 * @param data
 */
export const updateCryptoCoin = (data: CryptoCoinForm) => {
  return request({
    url: '/bicanmou/CryptoCoin',
    method: 'put',
    data: data
  });
};

/**
 * 删除币种信息
 * @param id
 */
export const delCryptoCoin = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoCoin/' + id,
    method: 'delete'
  });
};
