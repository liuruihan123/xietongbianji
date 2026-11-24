import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TuneAttachVO, TuneAttachForm, TuneAttachQuery } from '@/api/system/tuneAttach/types';

/**
 * 查询微调附件列表
 * @param query
 * @returns {*}
 */

export const listTuneAttach = (query?: TuneAttachQuery): AxiosPromise<TuneAttachVO[]> => {
  return request({
    url: '/system/tuneAttach/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询微调附件详细
 * @param id
 */
export const getTuneAttach = (id: string | number): AxiosPromise<TuneAttachVO> => {
  return request({
    url: '/system/tuneAttach/' + id,
    method: 'get'
  });
};

/**
 * 新增微调附件
 * @param data
 */
export const addTuneAttach = (data: TuneAttachForm) => {
  return request({
    url: '/system/tuneAttach',
    method: 'post',
    data: data
  });
};

/**
 * 修改微调附件
 * @param data
 */
export const updateTuneAttach = (data: TuneAttachForm) => {
  return request({
    url: '/system/tuneAttach',
    method: 'put',
    data: data
  });
};

/**
 * 删除微调附件
 * @param id
 */
export const delTuneAttach = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/tuneAttach/' + id,
    method: 'delete'
  });
};
