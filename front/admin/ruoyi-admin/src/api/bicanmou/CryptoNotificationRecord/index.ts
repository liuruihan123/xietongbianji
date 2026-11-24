import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoNotificationRecordVO, CryptoNotificationRecordForm, CryptoNotificationRecordQuery } from '@/api/bicanmou/CryptoNotificationRecord/types';

/**
 * 查询通知记录列表
 * @param query
 * @returns {*}
 */

export const listCryptoNotificationRecord = (query?: CryptoNotificationRecordQuery): AxiosPromise<CryptoNotificationRecordVO[]> => {
  return request({
    url: '/bicanmou/CryptoNotificationRecord/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询通知记录详细
 * @param id
 */
export const getCryptoNotificationRecord = (id: string | number): AxiosPromise<CryptoNotificationRecordVO> => {
  return request({
    url: '/bicanmou/CryptoNotificationRecord/' + id,
    method: 'get'
  });
};

/**
 * 新增通知记录
 * @param data
 */
export const addCryptoNotificationRecord = (data: CryptoNotificationRecordForm) => {
  return request({
    url: '/bicanmou/CryptoNotificationRecord',
    method: 'post',
    data: data
  });
};

/**
 * 修改通知记录
 * @param data
 */
export const updateCryptoNotificationRecord = (data: CryptoNotificationRecordForm) => {
  return request({
    url: '/bicanmou/CryptoNotificationRecord',
    method: 'put',
    data: data
  });
};

/**
 * 删除通知记录
 * @param id
 */
export const delCryptoNotificationRecord = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoNotificationRecord/' + id,
    method: 'delete'
  });
};
