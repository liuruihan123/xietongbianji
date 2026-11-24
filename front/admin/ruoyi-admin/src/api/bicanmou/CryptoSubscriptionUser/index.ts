import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoSubscriptionUserVO, CryptoSubscriptionUserForm, CryptoSubscriptionUserQuery } from '@/api/bicanmou/CryptoSubscriptionUser/types';

/**
 * 查询订阅用户列表
 * @param query
 * @returns {*}
 */

export const listCryptoSubscriptionUser = (query?: CryptoSubscriptionUserQuery): AxiosPromise<CryptoSubscriptionUserVO[]> => {
  return request({
    url: '/bicanmou/CryptoSubscriptionUser/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询订阅用户详细
 * @param id
 */
export const getCryptoSubscriptionUser = (id: string | number): AxiosPromise<CryptoSubscriptionUserVO> => {
  return request({
    url: '/bicanmou/CryptoSubscriptionUser/' + id,
    method: 'get'
  });
};

/**
 * 新增订阅用户
 * @param data
 */
export const addCryptoSubscriptionUser = (data: CryptoSubscriptionUserForm) => {
  return request({
    url: '/bicanmou/CryptoSubscriptionUser',
    method: 'post',
    data: data
  });
};

/**
 * 修改订阅用户
 * @param data
 */
export const updateCryptoSubscriptionUser = (data: CryptoSubscriptionUserForm) => {
  return request({
    url: '/bicanmou/CryptoSubscriptionUser',
    method: 'put',
    data: data
  });
};

/**
 * 删除订阅用户
 * @param id
 */
export const delCryptoSubscriptionUser = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoSubscriptionUser/' + id,
    method: 'delete'
  });
};
