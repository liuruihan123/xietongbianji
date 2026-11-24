import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoNotificationLogVO, CryptoNotificationLogForm, CryptoNotificationLogQuery } from '@/api/bicanmou/CryptoNotificationLog/types';

/**
 * 查询新币通知记录列表
 * @param query
 * @returns {*}
 */

export const listCryptoNotificationLog = (query?: CryptoNotificationLogQuery): AxiosPromise<CryptoNotificationLogVO[]> => {
  return request({
    url: '/bicanmou/CryptoNotificationLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询新币通知记录详细
 * @param id
 */
export const getCryptoNotificationLog = (id: string | number): AxiosPromise<CryptoNotificationLogVO> => {
  return request({
    url: '/bicanmou/CryptoNotificationLog/' + id,
    method: 'get'
  });
};

/**
 * 新增新币通知记录
 * @param data
 */
export const addCryptoNotificationLog = (data: CryptoNotificationLogForm) => {
  return request({
    url: '/bicanmou/CryptoNotificationLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改新币通知记录
 * @param data
 */
export const updateCryptoNotificationLog = (data: CryptoNotificationLogForm) => {
  return request({
    url: '/bicanmou/CryptoNotificationLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除新币通知记录
 * @param id
 */
export const delCryptoNotificationLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoNotificationLog/' + id,
    method: 'delete'
  });
};
