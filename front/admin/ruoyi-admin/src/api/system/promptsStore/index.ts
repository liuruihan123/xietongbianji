import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { PromptsStoreVO, PromptsStoreForm, PromptsStoreQuery } from '@/api/system/promptsStore/types';

/**
 * 查询提示词商店列表
 * @param query
 * @returns {*}
 */

export const listPromptsStore = (query?: PromptsStoreQuery): AxiosPromise<PromptsStoreVO[]> => {
  return request({
    url: '/system/promptsStore/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询提示词商店详细
 * @param id
 */
export const getPromptsStore = (id: string | number): AxiosPromise<PromptsStoreVO> => {
  return request({
    url: '/system/promptsStore/' + id,
    method: 'get'
  });
};

/**
 * 新增提示词商店
 * @param data
 */
export const addPromptsStore = (data: PromptsStoreForm) => {
  return request({
    url: '/system/promptsStore',
    method: 'post',
    data: data
  });
};

/**
 * 修改提示词商店
 * @param data
 */
export const updatePromptsStore = (data: PromptsStoreForm) => {
  return request({
    url: '/system/promptsStore',
    method: 'put',
    data: data
  });
};

/**
 * 删除提示词商店
 * @param id
 */
export const delPromptsStore = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/promptsStore/' + id,
    method: 'delete'
  });
};
