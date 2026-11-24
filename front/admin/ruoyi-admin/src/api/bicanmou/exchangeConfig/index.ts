import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ExchangeConfigVO, ExchangeConfigForm, ExchangeConfigQuery } from '@/api/bicanmou/exchangeConfig/types';

/**
 * 查询交易所配置列表
 * @param query
 * @returns {*}
 */

export const listExchangeConfig = (query?: ExchangeConfigQuery): AxiosPromise<ExchangeConfigVO[]> => {
  return request({
    url: '/bicanmou/exchangeConfig/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询交易所配置详细
 * @param id
 */
export const getExchangeConfig = (id: string | number): AxiosPromise<ExchangeConfigVO> => {
  return request({
    url: '/bicanmou/exchangeConfig/' + id,
    method: 'get'
  });
};

/**
 * 新增交易所配置
 * @param data
 */
export const addExchangeConfig = (data: ExchangeConfigForm) => {
  return request({
    url: '/bicanmou/exchangeConfig',
    method: 'post',
    data: data
  });
};

/**
 * 修改交易所配置
 * @param data
 */
export const updateExchangeConfig = (data: ExchangeConfigForm) => {
  return request({
    url: '/bicanmou/exchangeConfig',
    method: 'put',
    data: data
  });
};

/**
 * 删除交易所配置
 * @param id
 */
export const delExchangeConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/exchangeConfig/' + id,
    method: 'delete'
  });
};
