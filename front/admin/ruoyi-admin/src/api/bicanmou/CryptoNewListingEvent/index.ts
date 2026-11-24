import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoNewListingEventVO, CryptoNewListingEventForm, CryptoNewListingEventQuery } from '@/api/bicanmou/CryptoNewListingEvent/types';

/**
 * 查询新币上市事件列表
 * @param query
 * @returns {*}
 */

export const listCryptoNewListingEvent = (query?: CryptoNewListingEventQuery): AxiosPromise<CryptoNewListingEventVO[]> => {
  return request({
    url: '/bicanmou/CryptoNewListingEvent/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询新币上市事件详细
 * @param id
 */
export const getCryptoNewListingEvent = (id: string | number): AxiosPromise<CryptoNewListingEventVO> => {
  return request({
    url: '/bicanmou/CryptoNewListingEvent/' + id,
    method: 'get'
  });
};

/**
 * 新增新币上市事件
 * @param data
 */
export const addCryptoNewListingEvent = (data: CryptoNewListingEventForm) => {
  return request({
    url: '/bicanmou/CryptoNewListingEvent',
    method: 'post',
    data: data
  });
};

/**
 * 修改新币上市事件
 * @param data
 */
export const updateCryptoNewListingEvent = (data: CryptoNewListingEventForm) => {
  return request({
    url: '/bicanmou/CryptoNewListingEvent',
    method: 'put',
    data: data
  });
};

/**
 * 删除新币上市事件
 * @param id
 */
export const delCryptoNewListingEvent = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoNewListingEvent/' + id,
    method: 'delete'
  });
};
